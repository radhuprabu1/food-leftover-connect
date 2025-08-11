package com.app.common.dto;

/**
 * Represents the lifecycle status of a {@link FoodListing}.
 */
public enum FoodListingStatus {
    /**
     * The food donation has been accepted by a receiver.
     */
    ACCEPTED,
    /**
     * The food donation has passed its expiry time and is no longer available.
     */
    EXPIRED
}