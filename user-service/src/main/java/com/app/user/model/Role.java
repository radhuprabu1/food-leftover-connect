package com.app.user.model;

/**
 * Represents the distinct roles a user can have within the Food Leftover Connect application.
 */
public enum Role {
	/**
	 * Represents a user who donates food.
	 */
	ROLE_DONOR,
	/**
	 * Represents a user or organization (e.g., NGO) that receives food donations.
	 */
	ROLE_RECEIVER
}