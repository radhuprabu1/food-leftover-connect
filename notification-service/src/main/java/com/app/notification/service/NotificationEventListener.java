package com.app.notification.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.app.common.event.FindNearbyReceiversRequest;
import com.app.common.event.FoodAcceptedEvent;
import com.app.common.event.FoodCollectedEvent;
import com.app.common.event.FoodListedEvent;
import com.app.common.event.FoodListingCancellationByReceiverEvent;
import com.app.common.event.FoodListingCancelledEvent;
import com.app.common.event.FoodListingRemovedEvent;
import com.app.common.event.MarkAsCollectedCommand;
import com.app.common.event.NearbyReceiversResponse;
import com.app.common.event.SendFoodAlertEvent;
import com.app.common.event.UserRegisteredEvent;

/**
 * The primary event listener for the notification-service.
 * <p>
 * The {@code @RabbitListener} on the class creates one consumer for the 'notifications.queue'.
 * The {@code @RabbitHandler} methods inside allow this single consumer to process different
 * types of events it receives from various exchanges.
 *
 * @author Radhakrishnan
 * @version 1.0
 */
@Service
@RabbitListener(queues = "notifications.queue")
public class NotificationEventListener {

	private static final Logger log = LoggerFactory.getLogger(NotificationEventListener.class);

	/**
	 * Handles a broad event indicating a new food donation is available.
	 * This could be used for a general "activity feed" in the future.
	 *
	 * @param event The event carrying details of the new food listing.
	 */
	@RabbitHandler
	public void handleFoodListedEvent(FoodListedEvent event) {
		log.info("NOTIFICATION-SERVICE [BROADCAST]: New food donation '{}' listed by donor {}.", event.foodName(), event.donor().id());
	}

	/**
	 * Handles an event indicating a receiver has accepted a food donation.
	 * The primary action is to notify the original donor.
	 *
	 * @param event The event carrying details of the acceptance.
	 */
	@RabbitHandler
	public void handleFoodAcceptedEvent(FoodAcceptedEvent event) {
		log.info("NOTIFICATION-SERVICE [ACTION]: Food listing {} was accepted by receiver {}. Simulating confirmation email to donor {}.",
				event.foodListingId(), event.receiver().id(), event.donorId());
	}

	/**
	 * Handles a targeted command to send a real-time alert to a specific receiver.
	 *
	 * @param event The command event containing the target receiver and food details.
	 */
	@RabbitHandler
	public void handleSendFoodAlertEvent(SendFoodAlertEvent event) {
		log.info("NOTIFICATION-SERVICE [TARGETED ALERT]: Simulating push notification to receiver {} about new food '{}' (ID: {}).",
				event.receiverId(), event.foodName(), event.foodListingId());
	}

	/**
	 * Handles an event indicating a new user has joined the platform.
	 * The primary action is to send a welcome notification.
	 *
	 * @param event The event carrying the new user's details.
	 */
	@RabbitHandler
	public void handleUserRegisteredEvent(UserRegisteredEvent event) {
		log.info("NOTIFICATION-SERVICE [ACTION]: New user '{}' (ID: {}) has registered. Simulating sending welcome email.",
				event.user().name(), event.user().id());
	}

	@RabbitHandler
	public void handleMarkAsCollectedCommand(MarkAsCollectedCommand command) {
		// The notification service doesn't need to act on this internal command.
		// We add the handler simply to acknowledge and ignore the message.
		log.info("NOTIFICATION-SERVICE: Ignoring MarkAsCollectedCommand for food listing {}.", command.foodListingId());
	}

	@RabbitHandler
	public void handleFoodCollectedEvent(FoodCollectedEvent event) {
		// We could, for example, send a final "Thank You" notification to both parties.
		log.info("NOTIFICATION-SERVICE [ACTION]: Food listing {} has been collected. Simulating 'Thank You' notifications.", event.foodListingId());
	}

	/**
	 * Handles an event indicating a food listing was permanently removed.
	 * This could be used to send a final "archive" notification if needed.
	 *
	 * @param event The event containing the ID of the removed listing.
	 */
	@RabbitHandler
	public void handleFoodListingRemovedEvent(FoodListingRemovedEvent event) {
		log.info("NOTIFICATION-SERVICE [INFO]: Food listing {} was removed by cleanup job.", event.foodListingId());
	}

	@RabbitHandler
	public void handleFoodListingCancelledEvent(FoodListingCancelledEvent event) {
		if (event.receiverId() != null) {
			log.warn("NOTIFICATION-SERVICE [ACTION]: Donor {} cancelled listing {}. Simulating cancellation alert to receiver {}.",
					event.donorId(), event.foodListingId(), event.receiverId());
		} else {
			log.info("NOTIFICATION-SERVICE [INFO]: Unaccepted listing {} was cancelled by donor {}.",
					event.foodListingId(), event.donorId());
		}
	}
	
	@RabbitHandler
	public void handleFoodListingCancellationByReceiverEvent(FoodListingCancellationByReceiverEvent event) {
	    log.info("NOTIFICATION-SERVICE [ACTION]: Receiver {} cancelled acceptance of listing {}. Simulating notification to donor.",
	            event.receiverId(), event.foodListingId());
	}
	
    /**
     * Handles the request to find nearby receivers.
     * The notification-service does not need to act on this, so we just log and ignore.
     *
     * @param request The request to find receivers.
     */
    @RabbitHandler
    public void handleFindNearbyReceiversRequest(FindNearbyReceiversRequest request) {
        log.info("NOTIFICATION-SERVICE: Ignoring FindNearbyReceiversRequest with correlation ID {}.", request.correlationId());
    }

    /**
     * Handles the response containing nearby receivers.
     * The notification-service does not need to act on this, so we just log and ignore.
     *
     * @param response The response containing the list of receivers.
     */
    @RabbitHandler
    public void handleNearbyReceiversResponse(NearbyReceiversResponse response) {
        log.info("NOTIFICATION-SERVICE: Ignoring NearbyReceiversResponse with correlation ID {}.", response.correlationId());
    }
}