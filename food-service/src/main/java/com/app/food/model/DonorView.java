package com.app.food.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * A local, replicated view of a Food Donor's public information.
 * <p>
 * This table is populated by listening to events from the user-service. It allows the
 * donor-service to be autonomous, holding the data it needs to perform its
 * functions without making live API calls to the user-service.
 *
 * @author Radhakrishnan
 * @version 1.0
 */
@Entity
@Table(name = "donor_views")
@Getter
@Setter
public class DonorView {

	/**
	 * The unique identifier for the donor. This is the same as the User ID.
	 */
	@Id
	private Long id;

	/**
	 * The donor's name.
	 */
	private String name;

	/**
	 * The geographical latitude of the donor's location.
	 */
	private Double latitude;

	/**
	 * The geographical longitude of the donor's location.
	 */
	private Double longitude;
}