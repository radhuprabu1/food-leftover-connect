package com.app.common.event;

import com.app.common.dto.DonorDTO;

import java.time.LocalDateTime;

/**
 * An event that is published when a food donor successfully lists a new food donation.
 * This event is broadcast to all interested services.
 *
 * @param foodListingId  The unique ID of the newly created food listing.
 * @param foodName       The name of the food.
 * @param quantity       An integer representing how many people the food can serve.
 * @param donor          A DTO containing the public details of the donor.
 * @param expiryDateTime The timestamp when the food will expire.
 */
public record FoodListedEvent(
		Long foodListingId,
		String foodName,
		int quantity,
		DonorDTO donor,
		LocalDateTime expiryDateTime
		) {}