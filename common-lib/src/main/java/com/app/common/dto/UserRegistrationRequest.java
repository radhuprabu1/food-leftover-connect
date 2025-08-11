package com.app.common.dto;

/**
 * A Data Transfer Object representing the request body for a new user registration.
 * This DTO is specific to the API request and includes sensitive information like the password.
 *
 * @param name          The full name of the user.
 * @param email         The email address for the user (will be their username).
 * @param password      The user's chosen password (will be hashed by the user-service).
 * @param address       The user's physical address.
 * @param contactNumber The user's contact phone number.
 * @param role          The role the user is registering for ("ROLE_DONOR" or "ROLE_RECEIVER").
 * @param location      The user's geographical location.
 */
public record UserRegistrationRequest(
    String name,
    String email,
    String password,
    String address,
    String contactNumber,
    String role,
    LocationDTO location
) {}