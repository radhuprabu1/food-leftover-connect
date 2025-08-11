package com.app.user.repository;

import com.app.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for the {@link User} entity.
 * This interface provides all standard CRUD (Create, Read, Update, Delete) operations
 * and allows for the definition of custom query methods.
 *
 * @author Radhakrishnan
 * @version 1.0
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	/**
	 * Finds a user by their unique email address.
	 * This is a critical method for checking if a user already exists during registration
	 * and for fetching user details during login.
	 *
	 * @param email The email address to search for.
	 * @return An {@link Optional} containing the found {@link User}, or an empty Optional if no user is found.
	 */
	Optional<User> findByEmail(String email);
}