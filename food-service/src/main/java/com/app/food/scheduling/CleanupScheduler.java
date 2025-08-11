package com.app.food.scheduling;

import com.app.food.service.FoodListingService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * A scheduled service that performs periodic cleanup tasks for the donor-service.
 *
 * @author Radhakrishnan
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class CleanupScheduler {

    private static final Logger log = LoggerFactory.getLogger(CleanupScheduler.class);
    private final FoodListingService foodListingService;

    /**
     * A scheduled task that runs daily at 2:00 AM server time.
     * The cron expression "0 0 2 * * *" means:
     * second(0), minute(0), hour(2), day-of-month(*), month(*), day-of-week(*)
     * This task cleans up old, completed, and expired food listings.
     */
    @Scheduled(cron = "0 */10 * * * *")
    public void cleanupOldListings() {
        log.info("SCHEDULER: Running daily cleanup job for old food listings...");
        foodListingService.cleanupListings();
    }
}