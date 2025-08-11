package com.app.user.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.common.config.MessagingConfigConstants;
import com.app.common.dto.UserDTO;
import com.app.common.dto.UserRegistrationRequest;
import com.app.common.event.UserRegisteredEvent;
import com.app.user.mapper.UserMapper;
import com.app.user.model.User;
import com.app.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * Service class for user management, now also acting as the UserDetailsService for Spring Security.
 *
 * @author Radhakrishnan
 * @version 1.1
 */
@Service
@RequiredArgsConstructor
public class UserService {

	private static final Logger log = LoggerFactory.getLogger(UserService.class);
	private final UserRepository userRepository;
	private final RabbitTemplate rabbitTemplate;

	/**
	 * Handles the registration of a new user, now with password hashing.
	 *
	 * @param request The user registration request data.
	 * @return A DTO of the newly created user.
	 * @throws IllegalStateException if a user with the given email already exists.
	 */
	@Transactional
	public UserDTO registerUser(UserRegistrationRequest request) {
		userRepository.findByEmail(request.email()).ifPresent(user -> {
			throw new IllegalStateException("User with email " + request.email() + " already exists.");
		});

		User userToSave = UserMapper.toUserEntity(request);

		userToSave.setPassword(request.password());

		User savedUser = userRepository.save(userToSave);
		log.info("New user saved to database with ID: {}", savedUser.getId());

		UserDTO userDTO = UserMapper.toUserDTO(savedUser);
		UserRegisteredEvent event = new UserRegisteredEvent(userDTO);

		rabbitTemplate.convertAndSend(MessagingConfigConstants.USER_EVENTS_EXCHANGE, "", event);
		log.info("Published UserRegisteredEvent for user ID: {}", savedUser.getId());

		return userDTO;
	}

}