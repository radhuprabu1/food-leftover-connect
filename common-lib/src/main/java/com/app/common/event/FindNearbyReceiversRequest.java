package com.app.common.event;

import com.app.common.dto.LocationDTO;

/**
 * An event published by a service (like donor-service) to request a list of
 * receivers near a specific location.
 *
 * @param correlationId A unique ID to track this specific request.
 * @param location      The location to search around.
 * @param radiusKm      The search radius in kilometers.
 */
public record FindNearbyReceiversRequest(
    String correlationId,
    LocationDTO location,
    Double radiusKm
) {}