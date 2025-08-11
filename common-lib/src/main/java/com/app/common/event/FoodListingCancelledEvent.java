package com.app.common.event;

/**
 * An event published when a donor cancels their own food listing.
 *
 * @param foodListingId The ID of the cancelled listing.
 * @param donorId       The ID of the donor who cancelled it.
 * @param receiverId    The ID of the receiver (if it was already accepted), can be null.
 */
public record FoodListingCancelledEvent(
    Long foodListingId,
    Long donorId,
    Long receiverId
) {}