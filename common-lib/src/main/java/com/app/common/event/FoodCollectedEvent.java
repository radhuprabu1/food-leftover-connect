package com.app.common.event;

/**
 * An event published when a donor confirms that a food donation has been physically collected.
 * This event signals other services to perform cleanup on their local views of the listing.
 *
 * @param foodListingId The unique ID of the food listing that was collected.
 */
public record FoodCollectedEvent(
    Long foodListingId
) {}