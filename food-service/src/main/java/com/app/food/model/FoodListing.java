package com.app.food.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.UpdateTimestamp;

import com.app.common.dto.FoodListingStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a single food donation listing created by a donor.
 * This entity is the source of truth for all food donations in the system.
 *
 * @author Radhakrishnan
 * @version 1.1
 */
@Entity
@Table(name = "food_listings")
@Getter
@Setter
public class FoodListing {

    /**
     * The unique identifier for the food listing.
     * Generated automatically by the database.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The ID of the donor who created this listing.
     * This links the donation back to a user in the user-service.
     */
    @Column(nullable = false)
    private Long donorId;

    /**
     * The descriptive name of the food (e.g., "Vegetable Curry and Rice").
     */
    private String foodName;

    /**
     * The timestamp of when the food was prepared.
     */
    private LocalDateTime preparedDateTime;

    /**
     * An integer representing the estimated number of people this food can serve.
     */
    private int quantity;

    /**
     * The timestamp when the food donation expires and is no longer available.
     */
    private LocalDateTime expiryDateTime;

    /**
     * The current status of the listing (e.g., ACCEPTED, EXPIRED).
     * It is null when first created, indicating it is available.
     */
    @Enumerated(EnumType.STRING)
    private FoodListingStatus status;

    /**
     * The ID of the receiver who accepted this food listing.
     * It is null until a receiver accepts the donation.
     */
    private Long receiverId;
    
    /**
     * A flag indicating if the food has been physically collected by the receiver.
     * Defaults to false when the listing is created.
     */
    @Column(nullable = false)
    @ColumnDefault("false")
    private boolean isCollected = false;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    /**
     * The designated time for the receiver to pick up the food.
     */
    private LocalDateTime pickupTime;
}