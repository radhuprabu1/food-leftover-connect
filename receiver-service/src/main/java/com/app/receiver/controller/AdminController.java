package com.app.receiver.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.receiver.model.ReceiverView;
import com.app.receiver.model.Reminder;
import com.app.receiver.repository.ReceiverViewRepository;
import com.app.receiver.repository.ReminderRepository;

import lombok.RequiredArgsConstructor;

/**
 * REST Controller for admin-only endpoints related to the receiver domain.
 *
 * @author Radhakrishnan
 * @version 1.0
 */
@RestController("receiverAdminController")
@RequestMapping("/api/admin/receivers")
@RequiredArgsConstructor
public class AdminController {

	private final ReceiverViewRepository receiverViewRepository;
	private final ReminderRepository reminderRepository;

	/**
	 * Retrieves all receiver views stored locally in this service.
	 * In a secure context, this endpoint would be restricted to users with ROLE_ADMIN.
	 *
	 * @return A list of all receiver views.
	 */
	@GetMapping
	public ResponseEntity<List<ReceiverView>> getAllReceiverViews() {
		return ResponseEntity.ok(receiverViewRepository.findAll());
	}

	/**
	 * Retrieves all reminders currently stored in the system.
	 * Useful for admins to monitor pending or processed reminders.
	 *
	 * @return A list of all reminders.
	 */
	@GetMapping("/reminders")
	public ResponseEntity<List<Reminder>> getAllReminders() {
		return ResponseEntity.ok(reminderRepository.findAll());
	}
}