package com.app.user.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a user of the application, who can be either a Food Donor or a Food Receiver.
 * This entity is the single source of truth for all user-related data.
 *
 * @author Radhakrishnan
 * @version 1.0
 */
@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

	/**
	 * The unique identifier for the user.
	 * Generated automatically by the database.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * The full name of the user or organization.
	 */
	private String name;

	/**
	 * The user's email address. Must be unique as it will be used for login.
	 */
	@Column(unique = true, nullable = false)
	private String email;

	/**
	 * The user's password. This field will be stored as a secure hash
	 * once Spring Security is implemented.
	 */
	private String password;

	/**
	 * The physical address of the user.
	 */
	private String address;

	/**
	 * The contact phone number for the user.
	 */
	private String contactNumber;

	/**
	 * The role of the user, determining their permissions and capabilities.
	 * Stored as a string in the database (e.g., "ROLE_DONOR").
	 */
	@Enumerated(EnumType.STRING)
	private Role role;

	/**
	 * The geographical latitude of the user's location.
	 * Critical for location-based matching features.
	 */
	private Double latitude;

	/**
	 * The geographical longitude of the user's location.
	 * Critical for location-based matching features.
	 */
	private Double longitude;
}