package com.app.common.event;

/**
 * An event published when a receiver cancels their acceptance of a food listing.
 * This instructs the donor-service to make the listing available again.
 *
 * @param foodListingId The ID of the food listing being cancelled.
 * @param receiverId    The ID of the receiver who is cancelling.
 */
public record FoodListingCancellationByReceiverEvent(
    Long foodListingId,
    Long receiverId
) {}