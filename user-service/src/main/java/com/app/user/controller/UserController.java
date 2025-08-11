package com.app.user.controller;

import com.app.common.dto.UserDTO;
import com.app.common.dto.UserRegistrationRequest;
import com.app.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for handling all user-related API endpoints.
 *
 * @author Radhakrishnan
 * @version 1.0
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    /**
     * The service layer that contains the business logic for user operations.
     */
    private final UserService userService;

    /**
     * API endpoint for registering a new user (Donor or Receiver).
     *
     * @param registrationRequest The request body containing the new user's details.
     * @return A ResponseEntity containing the created user's public data and an HTTP status of 201 (Created).
     */
    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@RequestBody UserRegistrationRequest registrationRequest) {
        UserDTO newUser = userService.registerUser(registrationRequest);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }
}