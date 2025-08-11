package com.app.common.event;

import com.app.common.dto.UserDTO;

/**
 * An event that is published when a new user successfully registers in the system.
 *
 * @param user The public DTO of the newly registered user.
 */
public record UserRegisteredEvent(
		UserDTO user
		) {}