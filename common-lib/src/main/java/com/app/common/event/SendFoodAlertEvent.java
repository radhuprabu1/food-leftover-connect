package com.app.common.event;

/**
 * A specific, command-style event published by the receiver-service to instruct the
 * notification-service to send a real-time alert to a specific, matched receiver.
 *
 * @param receiverId    The unique ID of the target receiver for the notification.
 * @param foodListingId The ID of the relevant food listing.
 * @param foodName      The name of the food in the listing.
 */
public record SendFoodAlertEvent(
		Long receiverId,
		Long foodListingId,
		String foodName
		) {}