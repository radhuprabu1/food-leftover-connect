package com.app.common.event;

/**
 * An event published when a food listing is permanently removed from the system,
 * either because it expired or was completed and cleaned up.
 *
 * @param foodListingId The unique ID of the food listing that was removed.
 */
public record FoodListingRemovedEvent(
    Long foodListingId
) {}