package com.app.receiver.scheduling;

import com.app.receiver.model.Reminder;
import com.app.receiver.model.ReminderStatus;
import com.app.receiver.repository.ReminderRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

/**
 * A scheduled service that periodically checks for and processes due reminders.
 *
 * @author Radhakrishnan
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class ReminderScheduler {

	private static final Logger log = LoggerFactory.getLogger(ReminderScheduler.class);
	private final ReminderRepository reminderRepository;

	/**
	 * A scheduled task that runs at a fixed rate to find and process pending reminders.
	 * The {@code @Scheduled} annotation tells Spring to execute this method periodically.
	 * This task is transactional to ensure data consistency when updating reminders.
	 */
	@Scheduled(fixedRate = 60000) // Runs every 60 seconds
	@Transactional
	public void processPendingReminders() {
		log.trace("Scheduler running: Checking for pending reminders...");

		List<Reminder> dueReminders = reminderRepository.findByStatusAndReminderTimeBefore(
				ReminderStatus.PENDING,
				LocalDateTime.now()
				);

		if (dueReminders.isEmpty()) {
			return; // No work to do
		}

		log.info("Found {} due reminder(s) to process.", dueReminders.size());

		for (Reminder reminder : dueReminders) {
			log.info("SENDING REMINDER for food listing {} to receiver {}", reminder.getFoodListingId(), reminder.getReceiverId());
			// In a real application, this would trigger a targeted notification event.

			reminder.setStatus(ReminderStatus.PROCESSED);
			reminderRepository.save(reminder);
		}
	}
}