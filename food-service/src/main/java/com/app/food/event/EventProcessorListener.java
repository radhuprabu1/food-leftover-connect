package com.app.food.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.app.common.event.FindNearbyReceiversRequest;
import com.app.common.event.FoodAcceptedEvent;
import com.app.common.event.FoodListedEvent;
import com.app.common.event.FoodListingCancellationByReceiverEvent;
import com.app.common.event.FoodListingCancelledEvent;
import com.app.common.event.FoodListingRemovedEvent;
import com.app.common.event.MarkAsCollectedCommand;
import com.app.common.event.NearbyReceiversResponse;
import com.app.common.event.UserRegisteredEvent;
import com.app.food.model.DonorView;
import com.app.food.repository.DonorViewRepository;
import com.app.food.service.FoodListingService;

import lombok.RequiredArgsConstructor;

/**
 * A centralized event listener for the donor-service.
 * <p>
 * This class listens to a single queue that is bound to multiple exchanges.
 * The {@code @RabbitListener} annotation on the class creates a single consumer.
 * The {@code @RabbitHandler} annotation on the methods allows this single consumer
 * to delegate incoming messages to the correct handler based on the message's payload type.
 *
 * @author Radhakrishnan
 * @version 1.1
 */
@Service
@RequiredArgsConstructor
@RabbitListener(queues = "donor.updates.queue")
public class EventProcessorListener {

	/**
	 * Logger for this listener class.
	 */
	private static final Logger log = LoggerFactory.getLogger(EventProcessorListener.class);

	/**
	 * The service containing the business logic for food listings.
	 */
	private final FoodListingService foodListingService;

	/**
	 * The repository for the local view of donor data.
	 */
	private final DonorViewRepository donorViewRepository;

	/**
	 * Handles the {@link FoodAcceptedEvent} to update the status of a food listing.
	 * This is the "closing the loop" part of the workflow.
	 *
	 * @param event The event indicating a receiver has accepted a food donation.
	 */
	@RabbitHandler
	public void handleFoodAcceptedEvent(FoodAcceptedEvent event) {
		log.info("DONOR-SERVICE: Received FoodAcceptedEvent. Processing: {}", event);
		foodListingService.processFoodAcceptance(event);
	}

	/**
	 * Handles the {@link UserRegisteredEvent} to populate the local donor data view.
	 * This is the data replication part of the architecture.
	 *
	 * @param event The event indicating a new user has registered.
	 */
	@RabbitHandler
	public void handleUserRegisteredEvent(UserRegisteredEvent event) {
		log.info("DONOR-SERVICE: Received UserRegisteredEvent for user ID: {}", event.user().id());
		if ("ROLE_DONOR".equals(event.user().role())) {
			DonorView donorView = new DonorView();
			donorView.setId(event.user().id());
			donorView.setName(event.user().name());
			donorView.setLatitude(event.user().location().latitude());
			donorView.setLongitude(event.user().location().longitude());

			donorViewRepository.save(donorView);
			log.info("Saved new donor's view with ID: {}", donorView.getId());
		} else {
			log.info("Ignoring UserRegisteredEvent as the role is not ROLE_DONOR.");
		}
	}

	/**
	 * Handles the {@link FoodListedEvent}.
	 * This service is the original publisher of this event, so it receives a copy.
	 * The handler must exist to prevent a `NoSuchMethodException`, but it has no work to do.
	 *
	 * @param event The event indicating a new food listing was created.
	 */
	@RabbitHandler
	public void handleFoodListedEvent(FoodListedEvent event) {
		log.info("DONOR-SERVICE: Ignoring own FoodListedEvent for food listing ID {}.", event.foodListingId());
	}

	/**
	 * Handles the {@link MarkAsCollectedCommand} from a receiver.
	 * This triggers the donor-service to update the authoritative status of the food listing.
	 *
	 * @param command The command to mark a listing as collected.
	 */
	@RabbitHandler
	public void handleMarkAsCollectedCommand(MarkAsCollectedCommand command) {
		log.info("DONOR-SERVICE: Received command to mark listing {} as collected by receiver {}.",
				command.foodListingId(), command.receiverId());
		// We pass both IDs for a final authorization check in the service layer.
		foodListingService.markFoodAsCollected(command.foodListingId(), command.receiverId());
	}

	/**
	 * Handles the {@link FoodListingRemovedEvent}.
	 * This service is the original publisher of this event, so it just needs
	 * to acknowledge and ignore its own message to prevent errors.
	 *
	 * @param event The event indicating a food listing was removed.
	 */
	@RabbitHandler
	public void handleFoodListingRemovedEvent(FoodListingRemovedEvent event) {
		log.info("DONOR-SERVICE: Ignoring own FoodListingRemovedEvent for food listing ID {}.", event.foodListingId());
	}

	/**
	 * Handles the {@link FoodListingCancelledEvent}.
	 * This service is the original publisher, so it just ignores its own message.
	 *
	 * @param event The event indicating a food listing was cancelled.
	 */
	@RabbitHandler
	public void handleFoodListingCancelledEvent(FoodListingCancelledEvent event) {
		log.info("DONOR-SERVICE: Ignoring own FoodListingCancelledEvent for food listing ID {}.", event.foodListingId());
	}

	@RabbitHandler
	public void handleFoodListingCancellationByReceiverEvent(FoodListingCancellationByReceiverEvent event) {
		log.info("DONOR-SERVICE: Received cancellation request from receiver {} for listing {}",
				event.receiverId(), event.foodListingId());
		foodListingService.relistListingAfterCancellation(event.foodListingId(), event.receiverId());
	}

	/**
	 * Handles the response containing the list of nearby receivers.
	 * In a real application, this would push the results to the donor's UI via a WebSocket.
	 *
	 * @param response The response event from the receiver-service.
	 */
	@RabbitHandler
	public void handleNearbyReceiversResponse(NearbyReceiversResponse response) {
		log.info("DONOR-SERVICE: Received response for nearby receivers search. Correlation ID: {}", response.correlationId());
		// For now, we just log the results.
		response.receivers().forEach(receiver ->
		log.info("==> Nearby Receiver Found: ID={}, Name='{}'", receiver.id(), receiver.name())
				);
	}
	
    /**
     * Handles the request to find nearby receivers.
     * This service is the original publisher, so it just ignores its own message.
     */
    @RabbitHandler
    public void handleFindNearbyReceiversRequest(FindNearbyReceiversRequest request) {
        log.info("DONOR-SERVICE: Ignoring own FindNearbyReceiversRequest with correlation ID {}.", request.correlationId());
    }
}