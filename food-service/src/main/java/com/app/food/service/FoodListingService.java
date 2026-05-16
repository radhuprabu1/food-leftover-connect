package com.app.food.service;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.common.config.MessagingConfigConstants;
import com.app.common.dto.FoodCreationRequest;
import com.app.common.dto.FoodListingStatus;
import com.app.common.event.FoodAcceptedEvent;
import com.app.common.event.FoodCollectedEvent;
import com.app.common.event.FoodListedEvent;
import com.app.common.event.FoodListingCancelledEvent;
import com.app.common.event.FoodListingRemovedEvent;
import com.app.food.mapper.FoodListingMapper;
import com.app.food.model.DonorView;
import com.app.food.model.FoodListing;
import com.app.food.repository.DonorViewRepository;
import com.app.food.repository.FoodListingRepository;

import lombok.RequiredArgsConstructor;

/**
 * Service class containing the core business logic for managing food listings.
 *
 * @author Radhakrishnan
 * @version 1.1
 */
@Service
@RequiredArgsConstructor
public class FoodListingService {

	private static final Logger log = LoggerFactory.getLogger(FoodListingService.class);
	private final FoodListingRepository foodListingRepository;
	private final DonorViewRepository donorViewRepository;
	private final RabbitTemplate rabbitTemplate;

	/**
	 * Creates a new food listing, saves it to the database, and publishes a domain event.
	 *
	 * @param request The API request data for the new food listing.
	 * @param donorId The ID of the donor creating the listing (from security context).
	 * @return The persisted {@link FoodListing} entity.
	 * @throws IllegalStateException if the specified donor is not found in the local view.
	 */
	@Transactional
	public FoodListing listFood(FoodCreationRequest request, Long donorId) {
		// 1. Verify donor exists in our local view. This is fast and autonomous.
		DonorView donor = donorViewRepository.findById(donorId)
				.orElseThrow(() -> new IllegalStateException("Donor with ID " + donorId + " not found. The user may not be a donor or data is not yet synchronized."));

		// 2. Map DTO to Entity
		FoodListing foodListing = FoodListingMapper.toEntity(request, donorId);

		// 3. Save the food listing to the database
		FoodListing savedFoodListing = foodListingRepository.save(foodListing);
		log.info("Food listing saved with ID: {}", savedFoodListing.getId());

		// 4. Create the enriched event using data from our local DonorView
		FoodListedEvent event = FoodListingMapper.toFoodListedEvent(savedFoodListing, donor);

		// 5. Publish the event to the food events exchange
		rabbitTemplate.convertAndSend(MessagingConfigConstants.FOOD_EVENTS_EXCHANGE, "", event);
		log.info("Published rich FoodListedEvent for food listing ID: {}", event.foodListingId());

		return savedFoodListing;
	}

	// Inside FoodListingService.java

	/**
	 * Processes a cancellation from a receiver.
	 * If the cancellation is valid (not within 30 mins of pickup), it resets the
	 * listing's status and re-publishes the FoodListedEvent to make it available again.
	 *
	 * @param foodListingId The ID of the listing to relist.
	 * @param receiverId    The ID of the receiver who cancelled.
	 */
	@Transactional
	public void relistListingAfterCancellation(Long foodListingId, Long receiverId) {
	    log.info("Attempting to relist food listing {} after cancellation by receiver {}", foodListingId, receiverId);
	    FoodListing listing = foodListingRepository.findById(foodListingId)
	            .orElseThrow(() -> new IllegalStateException("Food listing not found."));

	    // Authorization check: Is the canceller the one who accepted it?
	    if (listing.getReceiverId() == null || !listing.getReceiverId().equals(receiverId)) {
	        throw new SecurityException("Receiver " + receiverId + " is not the one who accepted this listing.");
	    }

	    // Business Rule: Check if pickup time is within 30 minutes.
	    if (listing.getPickupTime() != null &&
	            LocalDateTime.now().isAfter(listing.getPickupTime().minusMinutes(30))) {
	        log.warn("Cancellation failed for listing {}: Pickup time is within 30 minutes.", foodListingId);
	        // In a real app, we might publish a "CancellationFailedEvent" here. For now, we just stop.
	        return;
	    }

	    // Reset the listing to be available again
	    listing.setStatus(null);
	    listing.setReceiverId(null);
	    FoodListing savedListing = foodListingRepository.save(listing);
	    log.info("Food listing {} has been reset and is available again.", foodListingId);

	    // REUSE: Re-publish the original FoodListedEvent to notify all services.
	    // This is the most powerful part of the pattern.
	    DonorView donor = donorViewRepository.findById(savedListing.getDonorId()).get();
	    FoodListedEvent relistEvent = FoodListingMapper.toFoodListedEvent(savedListing, donor);
	    rabbitTemplate.convertAndSend(MessagingConfigConstants.FOOD_EVENTS_EXCHANGE, "", relistEvent);
	    log.info("Re-published FoodListedEvent for ID {} to make it available to other receivers.", foodListingId);
	}
	
	/**
	 * Processes the acceptance of a food listing by a receiver.
	 * This method is triggered by a {@link FoodAcceptedEvent}.
	 *
	 * @param event The event containing details of the acceptance.
	 */
	@Transactional
	public void processFoodAcceptance(FoodAcceptedEvent event) {
		log.info("Processing acceptance for food listing ID: {}", event.foodListingId());

		FoodListing foodListing = foodListingRepository.findById(event.foodListingId())
				.orElseThrow(() -> new IllegalStateException("Food listing not found with ID: " + event.foodListingId()));

		// Idempotency check: If listing is already accepted, do nothing.
		if (foodListing.getStatus() != null) {
			log.warn("Food listing {} has already been processed. Current status: {}", foodListing.getId(), foodListing.getStatus());
			return;
		}

		// Update status and save
		foodListing.setStatus(FoodListingStatus.ACCEPTED);
		foodListing.setReceiverId(event.receiver().id());
		foodListingRepository.save(foodListing);

		log.info("Successfully updated food listing {} to ACCEPTED status for receiver {}.", foodListing.getId(), event.receiver().id());
	}
	
	@Transactional
	public FoodListing markFoodAsCollected(Long foodListingId, Long receiverId) {
	    log.info("Attempting to mark food listing {} as collected by receiver {}", foodListingId, receiverId);

	    FoodListing listing = foodListingRepository.findById(foodListingId)
	            .orElseThrow(() -> new IllegalStateException("Food listing with ID " + foodListingId + " not found."));

	    // Authorization check: Ensure the person marking as collected is the receiver who accepted it.
	    if (!listing.getReceiverId().equals(receiverId)) {
	        throw new SecurityException("Receiver " + receiverId + " is not the one who accepted this listing.");
	    }

	    // Idempotency check: Don't mark as collected if it already is.
	    if (listing.isCollected()) {
	        log.warn("Listing {} was already marked as collected. No action taken.", foodListingId);
	        return listing;
	    }

	    listing.setCollected(true);
	    FoodListing updatedListing = foodListingRepository.save(listing);
	    log.info("Food listing {} successfully marked as collected.", foodListingId);

	    // Publish an event to notify other services that this listing is now complete.
	    FoodCollectedEvent event = new FoodCollectedEvent(foodListingId);
	    rabbitTemplate.convertAndSend(MessagingConfigConstants.FOOD_EVENTS_EXCHANGE, "", event);
	    log.info("Published FoodCollectedEvent for food listing ID: {}", foodListingId);

	    return updatedListing;
	}
	
	/**
	 * Finds and permanently deletes all completed (collected) or expired food listings.
	 * For each deleted listing, it publishes a {@link FoodListingRemovedEvent} to ensure
	 * other services can perform their own cleanup.
	 */
	@Transactional
	public void cleanupListings() {
	    LocalDateTime now = LocalDateTime.now();
	    // Create a timestamp for 10 minutes ago
	    LocalDateTime tenMinutesAgo = now.minusMinutes(10);

	    // 1. Find all listings that are eligible for cleanup using the new, safer query.
	    List<FoodListing> listingsToCleanup = foodListingRepository.findListingsForCleanup(now, tenMinutesAgo);

	    if (listingsToCleanup.isEmpty()) {
	        log.info("CLEANUP: No food listings found for cleanup.");
	        return;
	    }

	    log.info("CLEANUP: Found {} food listing(s) to remove.", listingsToCleanup.size());

	    for (FoodListing listing : listingsToCleanup) {
	        // 2. For each listing, publish a removal event.
	        FoodListingRemovedEvent event = new FoodListingRemovedEvent(listing.getId());
	        rabbitTemplate.convertAndSend(MessagingConfigConstants.FOOD_EVENTS_EXCHANGE, "", event);
	        log.info("CLEANUP: Published FoodListingRemovedEvent for ID: {}", listing.getId());

	        // 3. Delete the listing from the source of truth.
	        foodListingRepository.delete(listing);
	        log.info("CLEANUP: Deleted food listing with ID: {}", listing.getId());
	    }
	}
	
	/**
	 * Allows a donor to cancel their food listing based on specific business rules.
	 * If cancellation is successful, it removes the listing and notifies the system.
	 *
	 * @param foodListingId The ID of the listing to cancel.
	 * @param donorId       The ID of the donor attempting the cancellation.
	 */
	@Transactional
	public void cancelFoodListing(Long foodListingId, Long donorId) {
	    log.info("Attempting cancellation for listing {} by donor {}", foodListingId, donorId);
	    FoodListing listing = foodListingRepository.findById(foodListingId)
	            .orElseThrow(() -> new IllegalStateException("Food listing not found."));

	    // Rule: Authorize that the user is the owner of the listing.
	    if (!listing.getDonorId().equals(donorId)) {
	        throw new SecurityException("User " + donorId + " is not authorized to cancel this listing.");
	    }

	    // --- ENFORCE BUSINESS RULES ---

	    // Condition 1: The listing is not yet accepted.
	    boolean isNotAccepted = (listing.getStatus() == null);

	    // Condition 2: The listing IS accepted, but there's more than 30 minutes until pickup.
	    boolean isCancellableIfAccepted = (listing.getStatus() == FoodListingStatus.ACCEPTED &&
	            listing.getPickupTime() != null &&
	            LocalDateTime.now().isBefore(listing.getPickupTime().minusMinutes(30)));

	    // If neither condition is met, the cancellation fails.
	    if (!isNotAccepted && !isCancellableIfAccepted) {
	        throw new IllegalStateException("This food listing cannot be cancelled at this time. It is either already accepted and pickup is imminent, or already collected.");
	    }

	    // --- CANCELLATION IS ALLOWED ---

	    // 1. Publish a specific event about the cancellation.
	    FoodListingCancelledEvent cancelledEvent = new FoodListingCancelledEvent(foodListingId, donorId, listing.getReceiverId());
	    rabbitTemplate.convertAndSend(MessagingConfigConstants.FOOD_EVENTS_EXCHANGE, "", cancelledEvent);
	    log.info("Published FoodListingCancelledEvent for ID: {}", foodListingId);

	    // 2. REUSE our existing cleanup event to trigger data removal everywhere.
	    FoodListingRemovedEvent removedEvent = new FoodListingRemovedEvent(foodListingId);
	    rabbitTemplate.convertAndSend(MessagingConfigConstants.FOOD_EVENTS_EXCHANGE, "", removedEvent);
	    log.info("Published FoodListingRemovedEvent for ID: {}", foodListingId);

	    // 3. Delete the listing from the source of truth.
	    foodListingRepository.delete(listing);
	    log.info("Successfully cancelled and deleted food listing with ID: {}", foodListingId);
	}
}