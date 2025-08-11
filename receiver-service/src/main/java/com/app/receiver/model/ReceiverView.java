package com.app.receiver.model;

import com.app.common.config.AppUtilConstants;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * A local, replicated view of a Food Receiver's public information.
 * <p>
 * This table is populated by listening to events from the user-service. It allows this
 * service to be autonomous and perform its location-based matching logic quickly and
 * efficiently without making live API calls.
 *
 * @author Radhakrishnan
 * @version 1.0
 */
@Entity
@Table(name = "receiver_views")
@Getter
@Setter
public class ReceiverView {

	/**
	 * The unique identifier for the receiver. This is the same as the User ID.
	 */
	@Id
	private Long id;

	/**
	 * The receiver's name.
	 */
	private String name;

	/**
	 * The geographical latitude of the receiver's location.
	 */
	private Double latitude;

	/**
	 * The geographical longitude of the receiver's location.
	 */
	private Double longitude;

	/**
	 * The default radius (in kilometers) within which this receiver wishes to be notified
	 * about new food listings.
	 */
	private Double notificationRadiusKm = AppUtilConstants.SEARCH_RADIUS_KM;
}