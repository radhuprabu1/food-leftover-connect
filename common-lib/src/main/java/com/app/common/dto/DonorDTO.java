package com.app.common.dto;

/**
 * A public-facing Data Transfer Object representing a Food Donor.
 * Contains only the information needed for other services to identify the donor and their location.
 *
 * @param id       The unique identifier of the donor (corresponds to the User ID).
 * @param name     The name of the donor.
 * @param location The geographical location of the donor.
 */
public record DonorDTO(
    Long id,
    String name,
    LocationDTO location
) {}