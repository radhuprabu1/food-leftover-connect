package com.app.common.dto;

/**
 * A Data Transfer Object representing a User's public profile information.
 * This is used in events to share user data between services. It explicitly omits sensitive data like passwords.
 *
 * @param id            The unique identifier for the user.
 * @param name          The user's full name.
 * @param email         The user's email address.
 * @param address       The user's physical address.
 * @param contactNumber The user's contact phone number.
 * @param role          The user's role in the system (e.g., "ROLE_DONOR", "ROLE_RECEIVER").
 * @param location      The user's geographical location.
 */
public record UserDTO(
    Long id,
    String name,
    String email,
    String address,
    String contactNumber,
    String role,
    LocationDTO location
) {}