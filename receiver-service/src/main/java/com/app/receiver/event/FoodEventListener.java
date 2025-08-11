package com.app.receiver.event;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.app.common.config.AppUtilConstants;
import com.app.common.config.MessagingConfigConstants;
import com.app.common.dto.LocationDTO;
import com.app.common.event.FoodAcceptedEvent;
import com.app.common.event.FoodListedEvent;
import com.app.common.event.FoodListingCancellationByReceiverEvent;
import com.app.common.event.FoodListingCancelledEvent;
import com.app.common.event.FoodListingRemovedEvent;
import com.app.common.event.MarkAsCollectedCommand;
import com.app.common.event.SendFoodAlertEvent;
import com.app.common.event.UserRegisteredEvent;
import com.app.receiver.model.FoodListingView;
import com.app.receiver.model.ReceiverView;
import com.app.receiver.repository.FoodListingViewRepository;
import com.app.receiver.repository.ReceiverViewRepository;
import com.app.receiver.repository.ReminderRepository;
import com.app.receiver.util.ReceiverUtils;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

/**
 * A centralized event listener for the receiver-service.
 * <p>
 * The {@code @RabbitListener} on the class creates one consumer for the specified queue.
 * The {@code @RabbitHandler} methods allow this consumer to process different event types.
 *
 * @author Radhakrishnan
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@RabbitListener(queues = "receiver.events.queue")
public class FoodEventListener {

	private static final Logger log = LoggerFactory.getLogger(FoodEventListener.class);
	private final ReceiverViewRepository receiverViewRepository;
	private final FoodListingViewRepository foodListingViewRepository;
	private final ReminderRepository reminderRepository;
	private final RabbitTemplate rabbitTemplate;

	/**
	 * Handles the {@link UserRegisteredEvent} to populate the local receiver data view.
	 *
	 * @param event The event indicating a new user has registered.
	 */
	@RabbitHandler
	public void handleUserRegisteredEvent(UserRegisteredEvent event) {
		log.info("RECEIVER-SERVICE: Received UserRegisteredEvent for user ID: {}", event.user().id());
		if ("ROLE_RECEIVER".equals(event.user().role())) {
			ReceiverView receiverView = new ReceiverView();
			receiverView.setId(event.user().id());
			receiverView.setName(event.user().name());
			receiverView.setLatitude(event.user().location().latitude());
			receiverView.setLongitude(event.user().location().longitude());
			// A default radius could be set here if desired
			receiverViewRepository.save(receiverView);
			log.info("Saved new receiver's view with ID: {}", receiverView.getId());
		}
	}

	/**
	 * Handles the {@link FoodListedEvent} to find matching nearby receivers.
	 *
	 * @param event The event indicating a new food listing has been created.
	 */
	@RabbitHandler
	public void handleFoodListedEvent(FoodListedEvent event) {
		log.info("RECEIVER-SERVICE: Received FoodListedEvent for '{}'. Finding nearby receivers...", event.foodName());
		log.info("RECEIVER-SERVICE: Received FoodListedEvent for '{}'. Storing local view...", event.foodName());

		// Create and save a complete local view of this food listing
		FoodListingView listingView = new FoodListingView();
		listingView.setId(event.foodListingId());
		listingView.setDonorId(event.donor().id());
		listingView.setFoodName(event.foodName());
		listingView.setQuantity(event.quantity());
		listingView.setDonorLatitude(event.donor().location().latitude());
		listingView.setDonorLongitude(event.donor().location().longitude());
		// Status is null by default, meaning it's available.
		foodListingViewRepository.save(listingView);
		log.info("Saved new food listing view with ID: {}", listingView.getId());
		LocationDTO donorLocation = event.donor().location();

		List<ReceiverView> allReceivers = receiverViewRepository.findAll();

		for (ReceiverView receiver : allReceivers) {
			double distance = ReceiverUtils.calculateDistance(
					donorLocation.latitude(), donorLocation.longitude(),
					receiver.getLatitude(), receiver.getLongitude()
					);

			if (distance <= AppUtilConstants.SEARCH_RADIUS_KM) {
				log.info("MATCH FOUND: Receiver {} is within {}km radius. Publishing targeted notification.", receiver.getId(), AppUtilConstants.SEARCH_RADIUS_KM);
				SendFoodAlertEvent alertEvent = new SendFoodAlertEvent(
						receiver.getId(),
						event.foodListingId(),
						event.foodName()
						);
				rabbitTemplate.convertAndSend(
						MessagingConfigConstants.NOTIFICATIONS_DIRECT_EXCHANGE,
						MessagingConfigConstants.FOOD_ALERT_ROUTING_KEY,
						alertEvent
						);
			}
		}
	}

	@RabbitHandler
	public void handleMarkAsCollectedCommand(MarkAsCollectedCommand command) {
		// This service publishes this command, so it just needs to ignore its own message.
		log.info("RECEIVER-SERVICE: Ignoring own MarkAsCollectedCommand for food listing ID {}.", command.foodListingId());
	}

	//    @RabbitHandler
	//    public void handleFoodCollectedEvent(FoodCollectedEvent event) {
	//        // This is the event that tells us to perform the final cleanup.
	//        // We implemented this in the last step, but it might have been missed.
	//        log.info("RECEIVER-SERVICE: Received FoodCollectedEvent for listing {}. Performing cleanup.", event.foodListingId());
	//        foodListingViewRepository.deleteById(event.foodListingId());
	//        reminderRepository.deleteByFoodListingId(event.foodListingId());
	//        log.info("Cleanup complete for food listing ID: {}", event.foodListingId());
	//    }
	@RabbitHandler
	public void handleFoodAcceptedEvent(FoodAcceptedEvent event) {
		log.info("RECEIVER-SERVICE: Received FoodAcceptedEvent for listing {}. Removing from local view.", event.foodListingId());
		// Once a listing is accepted, it's no longer available.
		// The simplest way to handle this is to delete it from our local view.
		foodListingViewRepository.deleteById(event.foodListingId());
	}

	/**
	 * Handles an event indicating a food listing has been permanently removed.
	 * This triggers a cleanup of all associated local data, such as listing views and reminders.
	 *
	 * @param event The event containing the ID of the removed listing.
	 */
	@RabbitHandler
	@Transactional
	public void handleFoodListingRemovedEvent(FoodListingRemovedEvent event) {
		log.info("RECEIVER-SERVICE: Received FoodListingRemovedEvent for listing {}. Performing cleanup.", event.foodListingId());
		foodListingViewRepository.deleteById(event.foodListingId());
		reminderRepository.deleteByFoodListingId(event.foodListingId());
		log.info("Cleanup complete for food listing ID: {}", event.foodListingId());
	}

	// Inside FoodEventListener.java in receiver-service
	@RabbitHandler
	public void handleFoodListingCancellationByReceiverEvent(FoodListingCancellationByReceiverEvent event) {
		log.info("RECEIVER-SERVICE: Ignoring own cancellation event for listing {}.", event.foodListingId());
	}


	@RabbitHandler
	public void handleFoodListingCancelledEvent(FoodListingCancelledEvent event) {
		// This handler can be used to trigger a specific UI update for the receiver
		// to inform them that a previously accepted item was cancelled by the donor.
		log.warn("RECEIVER-SERVICE: Received cancellation for food listing {}. Notifying relevant receiver {}.",
				event.foodListingId(), event.receiverId());
	}
}