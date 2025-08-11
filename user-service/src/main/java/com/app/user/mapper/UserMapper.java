package com.app.user.mapper;

import com.app.common.dto.LocationDTO;
import com.app.common.dto.UserDTO;
import com.app.common.dto.UserRegistrationRequest;
import com.app.user.model.Role;
import com.app.user.model.User;

/**
 * A utility class for mapping between User-related DTOs and the User JPA entity.
 * This helps to keep the service layer clean and focused on business logic by
 * abstracting away the conversion details.
 *
 * @author Radhakrishnan
 * @version 1.0
 */
public final class UserMapper {

	/**
	 * Private constructor to prevent instantiation of this utility class.
	 */
	private UserMapper() {}

	/**
	 * Maps a {@link User} entity to a public-facing {@link UserDTO}.
	 * This method ensures that sensitive information like the password is not exposed.
	 *
	 * @param user The saved User entity from the database.
	 * @return A new {@link UserDTO} instance containing public user data.
	 */
	public static UserDTO toUserDTO(User user) {
		return new UserDTO(
				user.getId(),
				user.getName(),
				user.getEmail(),
				user.getAddress(),
				user.getContactNumber(),
				user.getRole().name(),
				new LocationDTO(user.getLatitude(), user.getLongitude())
				);
	}

	/**
	 * Maps a {@link UserRegistrationRequest} DTO to a new {@link User} entity.
	 * This method prepares the entity object to be saved to the database.
	 *
	 * @param request The user registration request DTO from the API call.
	 * @return A new {@link User} entity, ready to be persisted.
	 */
	public static User toUserEntity(UserRegistrationRequest request) {
		User user = new User();
		user.setName(request.name());
		user.setEmail(request.email());
		user.setPassword(request.password());
		user.setAddress(request.address());
		user.setContactNumber(request.contactNumber());
		user.setRole(Role.valueOf(request.role()));
		user.setLatitude(request.location().latitude());
		user.setLongitude(request.location().longitude());
		return user;
	}
}