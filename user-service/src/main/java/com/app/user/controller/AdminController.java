package com.app.user.controller;

import com.app.common.dto.UserDTO;
import com.app.user.mapper.UserMapper;
import com.app.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for admin-only endpoints related to the user domain.
 *
 * @author Radhakrishnan
 * @version 1.0
 */
@RestController("userAdminController")
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;

    /**
     * Retrieves all users in the system.
     * This endpoint should be restricted to users with ROLE_ADMIN.
     *
     * @return A list of all users as public DTOs.
     */
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> allUsers = userRepository.findAll()
                .stream()
                .map(UserMapper::toUserDTO) // Convert entities to DTOs
                .collect(Collectors.toList());
        return ResponseEntity.ok(allUsers);
    }
}