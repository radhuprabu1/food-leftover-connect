package com.app.food.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.common.dto.FoodCreationRequest;
import com.app.food.model.FoodListing;
import com.app.food.service.FoodListingService;

import lombok.RequiredArgsConstructor;

/**
 * REST Controller for handling all food listing-related API endpoints.
 * All operations in this controller pertain to the actions of a Food Donor.
 *
 * @author Radhakrishnan
 * @version 1.1
 */
@RestController
@RequestMapping("/api/food-listings")
@RequiredArgsConstructor
public class FoodListingController {

	/**
	 * The service layer containing the business logic for food listing operations.
	 */
	private final FoodListingService foodListingService;

	/**
	 * API endpoint for a donor to list a new food donation.
	 * The donor's identity is passed via a header, simulating a security context.
	 *
	 * @param donorId The ID of the donor making the request.
	 * @param request The request body containing the new food listing's details.
	 * @return A ResponseEntity containing the created food listing and an HTTP status of 201 (Created).
	 */
	@PostMapping
	public ResponseEntity<FoodListing> listFood(
			@RequestHeader("X-User-Id") Long donorId,
			@RequestBody FoodCreationRequest request) {
		FoodListing listedFood = foodListingService.listFood(request, donorId);
		return new ResponseEntity<>(listedFood, HttpStatus.CREATED);
	}
	
	// Inside FoodListingController.java

	/**
	 * API endpoint for a donor to cancel their own food listing.
	 */
	@DeleteMapping("/{foodListingId}")
	public ResponseEntity<Void> cancelListing(
	        @PathVariable Long foodListingId,
	        @RequestHeader("X-User-Id") Long donorId) {
	    foodListingService.cancelFoodListing(foodListingId, donorId);
	    return ResponseEntity.noContent().build();
	}
}