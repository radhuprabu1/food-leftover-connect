package com.app.common.dto;

/**
 * A public-facing Data Transfer Object representing a Food Receiver (e.g., an NGO).
 * Contains only the information needed for other services, like notifying a donor.
 *
 * @param id       The unique identifier of the receiver (corresponds to the User ID).
 * @param name     The name of the receiver.
 * @param location The geographical location of the receiver.
 */
public record ReceiverDTO(
    Long id,
    String name,
    LocationDTO location,
    String address,
    String contactNumber
) {}