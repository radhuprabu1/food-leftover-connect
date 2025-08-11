package com.app.receiver.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.common.dto.ReceiverResponseDTO;
import com.app.receiver.model.FoodListingView;
import com.app.receiver.service.ReceiverService;

import lombok.RequiredArgsConstructor;

/**
 * REST Controller for handling all food receiver-related API endpoints.
 *
 * @author Radhakrishnan
 * @version 1.0
 */
@RestController
@RequestMapping("/api/receivers")
@RequiredArgsConstructor
public class ReceiverController {

	private final ReceiverService receiverService;

	/**
	 * API endpoint for a receiver to respond to a food listing alert.
	 * The receiver's identity is passed via a header, simulating a security context.
	 *
	 * @param foodListingId The ID of the food listing being responded to.
	 * @param receiverId    The ID of the receiver making the response.
	 * @param response      The request body containing the response type.
	 * @return An HTTP 200 OK response.
	 */
	@PostMapping("/responses/{foodListingId}")
	public ResponseEntity<Void> handleResponse(
			@PathVariable Long foodListingId,
			@RequestHeader("X-User-Id") Long receiverId,
			@RequestBody ReceiverResponseDTO response) {
		receiverService.processReceiverResponse(foodListingId, receiverId, response);
		return ResponseEntity.ok().build();
	}

	/**
	 * API endpoint for a receiver to get all available food listings within their vicinity.
	 *
	 * @param receiverId The ID of the receiver making the request (from security context).
	 * @return A ResponseEntity containing a list of nearby food listings.
	 */
	@GetMapping("/listings/available")
	public ResponseEntity<List<FoodListingView>> getAvailableListings(@RequestHeader("X-User-Id") Long receiverId) {
		List<FoodListingView> availableListings = receiverService.getAvailableFoodListings(receiverId);
		return ResponseEntity.ok(availableListings);
	}

	/**
	 * API endpoint for a receiver to confirm they have collected a food donation.
	 *
	 * @param foodListingId The ID of the food listing that was collected.
	 * @param receiverId    The ID of the receiver making the request (from security context).
	 * @return An HTTP 200 OK response.
	 */
	@PostMapping("/listings/{foodListingId}/collect")
	public ResponseEntity<Void> markAsCollected(
			@PathVariable Long foodListingId,
			@RequestHeader("X-User-Id") Long receiverId) {
		receiverService.confirmFoodCollection(foodListingId, receiverId);
		return ResponseEntity.ok().build();
	}

	/**
	 * API endpoint for a receiver to cancel their acceptance of a food listing.
	 *
	 * @param foodListingId The ID of the food listing to cancel.
	 * @param receiverId    The ID of the receiver making the request.
	 * @return An HTTP 204 No Content response.
	 */
	@DeleteMapping("/listings/{foodListingId}/acceptance")
	public ResponseEntity<Void> cancelAcceptedListing(
			@PathVariable Long foodListingId,
			@RequestHeader("X-User-Id") Long receiverId) {
		receiverService.cancelAcceptedListing(foodListingId, receiverId);
		return ResponseEntity.noContent().build();
	}
}