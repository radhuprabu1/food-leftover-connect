package com.app.receiver.repository;

import com.app.receiver.model.Reminder;
import com.app.receiver.model.ReminderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Spring Data JPA repository for the {@link Reminder} entity.
 *
 * @author Radhakrishnan
 * @version 1.0
 */
@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Long> {

	/**
	 * Custom query to find all reminders with a given status that were scheduled
	 * for a time before the current time.
	 *
	 * @param status        The status to search for (e.g., PENDING).
	 * @param currentTime   The current timestamp to compare against.
	 * @return A list of due reminders.
	 */
	List<Reminder> findByStatusAndReminderTimeBefore(ReminderStatus status, LocalDateTime currentTime);

	/**
	 * Deletes all Reminder entries associated with a specific food listing ID.
	 * This is a derived delete query provided by Spring Data JPA.
	 *
	 * @param foodListingId The ID of the food listing to clean up reminders for.
	 */
	void deleteByFoodListingId(Long foodListingId);
}