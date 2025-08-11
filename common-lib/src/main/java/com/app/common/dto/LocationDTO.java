package com.app.common.dto;

/**
 * A record representing a geographical location using latitude and longitude.
 * This is an immutable data structure.
 *
 * @param latitude  The latitude of the location.
 * @param longitude The longitude of the location.
 */
public record LocationDTO(
    Double latitude,
    Double longitude
) {}