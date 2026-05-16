package com.app.receiver.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.common.config.AppUtilConstants;
import com.app.common.config.MessagingConfigConstants;
import com.app.common.dto.LocationDTO;
import com.app.common.dto.ReceiverDTO;
import com.app.common.dto.ReceiverResponseDTO;
import com.app.common.event.FoodAcceptedEvent;
import com.app.common.event.FoodListingCancellationByReceiverEvent;
import com.app.common.event.MarkAsCollectedCommand;
import com.app.receiver.model.FoodListingView;
import com.app.receiver.model.ReceiverView;
import com.app.receiver.model.Reminder;
import com.app.receiver.model.ReminderStatus;
import com.app.receiver.repository.FoodListingViewRepository;
import com.app.receiver.repository.ReceiverViewRepository;
import com.app.receiver.repository.ReminderRepository;
import com.app.receiver.util.ReceiverUtils;

import lombok.RequiredArgsConstructor;

/**
 * Service class containing the core business logic for receiver actions.
 *
 * @author Radhakrishnan
 * @version 1.1
 */
@Service
@RequiredArgsConstructor
public class ReceiverService {

	private static final Logger log = LoggerFactory.getLogger(ReceiverService.class);
	private final RabbitTemplate rabbitTemplate;
	private final ReminderRepository reminderRepository;
	private final ReceiverViewRepository receiverViewRepository;
	private final FoodListingViewRepository foodListingViewRepository; // <-- Inject new repository

	/**
	 * Processes a receiver's response (Accept, Reject, Remind Later) to a food listing.
	 *
	 * @param foodListingId The ID of the food listing being responded to.
	 * @param receiverId    The ID of the receiver making the response (from security context).
	 * @param response      The DTO containing the response details.
	 */
	@Transactional
	public void processReceiverResponse(Long foodListingId, Long receiverId, ReceiverResponseDTO response) {
		switch (response.responseType()) {
		case ACCEPT:
			// 1. Fetch the local view of the food listing to get the donor's ID.
			FoodListingView listingView = foodListingViewRepository.findById(foodListingId)
			.orElseThrow(() -> new IllegalStateException("Food listing view with ID " + foodListingId + " not found."));
			Long donorId = listingView.getDonorId();

			// 2. Fetch the receiver's details from our local view to create a rich DTO.
			ReceiverView receiver = receiverViewRepository.findById(receiverId)
					.orElseThrow(() -> new IllegalStateException("Receiver with ID " + receiverId + " not found."));

			ReceiverDTO receiverDTO = new ReceiverDTO(
					receiver.getId(),
					receiver.getName(),
					new LocationDTO(receiver.getLatitude(), receiver.getLongitude()),
					receiver.getAddress(), receiver.getContactNumber()
					);

			log.info("Receiver {} ACCEPTED food listing {}. Publishing event.", receiverId, foodListingId);

			// 3. Create and publish the fully enriched event with the correct donorId.
			FoodAcceptedEvent event = new FoodAcceptedEvent(foodListingId, donorId, receiverDTO);
			rabbitTemplate.convertAndSend(MessagingConfigConstants.FOOD_EVENTS_EXCHANGE, "", event);
			break;
		case REJECT:
			log.info("Receiver {} REJECTED food listing {}.", receiverId, foodListingId);
			break;
		case REMIND_LATER:
			log.info("Receiver {} requested a reminder for food listing {} at {}.", receiverId, foodListingId, response.reminderTime());
			Reminder reminder = new Reminder();
			reminder.setFoodListingId(foodListingId);
			reminder.setReceiverId(receiverId);
			reminder.setReminderTime(response.reminderTime());
			reminder.setStatus(ReminderStatus.PENDING);
			reminderRepository.save(reminder);
			log.info("Reminder saved with ID: {}", reminder.getId());
			break;
		}
	}

	/**
	 * Finds all available food listings and filters them to show only those
	 * within a 5 km radius of the specified receiver.
	 *
	 * @param receiverId The ID of the receiver making the request.
	 * @return A list of {@link FoodListingView} objects that are available and nearby.
	 */
	public List<FoodListingView> getAvailableFoodListings(Long receiverId) {
		// 1. Get the receiver's location from our local view.
		ReceiverView receiver = receiverViewRepository.findById(receiverId)
				.orElseThrow(() -> new IllegalStateException("Receiver with ID " + receiverId + " not found."));

		// 2. Get all currently available food listings.
		// In our design, any listing present in the view table is considered available.
		List<FoodListingView> allAvailableListings = foodListingViewRepository.findAll();

		// 3. Filter the listings in memory to find ones within the radius.
		return allAvailableListings.stream()
				.filter(listing -> {
					Double distance = ReceiverUtils.calculateDistance(
							receiver.getLatitude(), receiver.getLongitude(),
							listing.getDonorLatitude(), listing.getDonorLongitude()
							);
					return distance <= AppUtilConstants.SEARCH_RADIUS_KM;
				})
				.toList();
	}

	/**
	 * Confirms that a receiver has collected a food donation and publishes a command
	 * to the donor-service to update the listing's master record.
	 *
	 * @param foodListingId The ID of the food listing.
	 * @param receiverId    The ID of the receiver confirming the collection.
	 */
	public void confirmFoodCollection(Long foodListingId, Long receiverId) {
	    // In a real app, we would first check our local database to ensure this receiver
	    // was the one who originally accepted this listing, as an authorization step.
	    log.info("Receiver {} is confirming collection of food listing {}. Publishing command.", receiverId, foodListingId);

	    MarkAsCollectedCommand command = new MarkAsCollectedCommand(foodListingId, receiverId);

	    // Publish the command to the exchange for the donor-service to process.
	    rabbitTemplate.convertAndSend(MessagingConfigConstants.FOOD_EVENTS_EXCHANGE, "", command);
	}
	
	/**
	 * Allows a receiver to cancel their accepted listing.
	 * It publishes an event to notify the donor-service to process the cancellation.
	 *
	 * @param foodListingId The ID of the listing to cancel.
	 * @param receiverId    The ID of the receiver initiating the cancellation.
	 */
	public void cancelAcceptedListing(Long foodListingId, Long receiverId) {
	    // Here, we could add a check against our local FoodListingView to see if this
	    // receiver was the one who accepted it, but the ultimate authority is the donor-service.
	    log.info("Receiver {} is cancelling acceptance for food listing {}. Publishing event.", receiverId, foodListingId);

	    FoodListingCancellationByReceiverEvent event = new FoodListingCancellationByReceiverEvent(foodListingId, receiverId);
	    rabbitTemplate.convertAndSend(MessagingConfigConstants.FOOD_EVENTS_EXCHANGE, "", event);
	}
	
}