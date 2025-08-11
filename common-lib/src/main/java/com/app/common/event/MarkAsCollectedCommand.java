package com.app.common.event;

/**
 * A command-style event published by the receiver-service to instruct the donor-service
 * to mark a specific food listing as collected.
 *
 * @param foodListingId The unique ID of the food listing to be marked as collected.
 * @param receiverId    The ID of the receiver who collected the food.
 */
public record MarkAsCollectedCommand(
    Long foodListingId,
    Long receiverId
) {}