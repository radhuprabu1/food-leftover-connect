package com.app.common.dto;

import java.time.LocalDateTime;

/**
 * A Data Transfer Object representing the request from a Donor to list a new food donation.
 * Contains only the details of the food itself. The donor's identity is determined from the security context.
 *
 * @param foodName         The descriptive name of the food (e.g., "Leftover Biryani and Sambar").
 * @param preparedDateTime The date and time the food was prepared.
 * @param quantity         An integer representing how many people the food can serve.
 * @param expiryDateTime   The date and time the food is considered expired and no longer safe to consume.
 */
public record FoodCreationRequest(
    String foodName,
    LocalDateTime preparedDateTime,
    Integer quantity,
    LocalDateTime expiryDateTime,
    LocalDateTime pickupTime
) {}