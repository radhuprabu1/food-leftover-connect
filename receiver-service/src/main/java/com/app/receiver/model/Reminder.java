package com.app.receiver.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

/**
 * Represents a scheduled reminder for a food receiver about a specific food listing.
 *
 * @author Radhakrishnan
 * @version 1.0
 */
@Entity
@Table(name = "reminders")
@Getter
@Setter
public class Reminder {

	/**
	 * The unique identifier for the reminder.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * The ID of the food listing this reminder is for.
	 */
	private Long foodListingId;

	/**
	 * The ID of the receiver who requested the reminder.
	 */
	private Long receiverId;

	/**
	 * The specific date and time the reminder should be triggered.
	 */
	private LocalDateTime reminderTime;

	/**
	 * The current status of the reminder (e.g., PENDING, PROCESSED).
	 */
	@Enumerated(EnumType.STRING)
	private ReminderStatus status;
}