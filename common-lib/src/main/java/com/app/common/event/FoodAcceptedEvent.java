package com.app.common.event;

import com.app.common.dto.ReceiverDTO;

/**
 * An event published when a food receiver officially accepts a food donation.
 * This event contains details about both the receiver and the original donor for notification purposes.
 *
 * @param foodListingId The ID of the food listing that was accepted.
 * @param donorId       The ID of the donor who originally listed the food.
 * @param receiver      A DTO containing the public details of the receiver who accepted the food.
 */
public record FoodAcceptedEvent(
		Long foodListingId,
		Long donorId,
		ReceiverDTO receiver
		) {}