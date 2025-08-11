package com.app.receiver.model;

/**
 * Represents the lifecycle status of a {@link Reminder}.
 */
public enum ReminderStatus {
	/**
	 * The reminder is scheduled and waiting to be processed.
	 */
	PENDING,
	/**
	 * The reminder has been sent and is complete.
	 */
	PROCESSED,
	/**
	 * The reminder was cancelled before being processed.
	 */
	CANCELLED
}