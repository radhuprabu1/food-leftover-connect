package com.app.receiver.model;

import com.app.common.dto.FoodListingStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * A local, replicated view of a Food Listing's essential details.
 * <p>
 * This allows the receiver-service to have the necessary context about a listing
 * without needing to query the donor-service.
 *
 * @author Radhakrishnan
 * @version 1.0
 */
@Entity
@Table(name = "food_listing_views")
@Getter
@Setter
public class FoodListingView {

    /**
     * The unique identifier of the food listing.
     */
    @Id
    private Long id;

    /**
     * The ID of the donor who created the listing.
     * This is crucial for linking acceptances back to the original donor.
     */
    private Long donorId;
    
    private String foodName;
    private Integer quantity;
    private Double donorLatitude;
    private Double donorLongitude;
    
    @Enumerated(EnumType.STRING)
    private FoodListingStatus status; // Can be null (AVAILABLE), or ACCEPTED
}