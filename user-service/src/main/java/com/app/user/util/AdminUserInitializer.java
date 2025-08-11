package com.app.user.util;

import com.app.user.model.Role;
import com.app.user.model.User;
import com.app.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * A component that runs on application startup to ensure a default
 * admin user exists in the database.
 *
 * @author Radhakrishnan
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
public class AdminUserInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminUserInitializer.class);
    private final UserRepository userRepository;

    /**
     * This method is executed automatically by Spring Boot after the application context is loaded.
     */
    @Override
    public void run(String... args) throws Exception {
        final String adminEmail = "admin1@foodconnect.com";

        // Check if the admin user already exists
        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            log.info("No admin user found. Creating default admin user...");

            User adminUser = new User();
            adminUser.setName("Admin_User1");
            adminUser.setEmail(adminEmail);
            // In a secure app, use: passwordEncoder.encode("admin123")
            adminUser.setPassword("admin123");
            adminUser.setRole(Role.ROLE_ADMIN);
            adminUser.setAddress("System");
            adminUser.setContactNumber("0000000000");

            userRepository.save(adminUser);
            log.info("Default admin user created with email: {}", adminEmail);
        } else {
            log.info("Admin user already exists.");
        }
    }
}