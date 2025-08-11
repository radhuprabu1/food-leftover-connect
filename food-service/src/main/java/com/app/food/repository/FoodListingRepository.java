package com.app.food.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.app.food.model.FoodListing;

/**
 * Spring Data JPA repository for the {@link FoodListing} entity.
 * Provides standard CRUD operations for food listings.
 *
 * @author Radhakrishnan
 * @version 1.0
 */
@Repository
public interface FoodListingRepository extends JpaRepository<FoodListing, Long> {

	/**
	 * Finds all listings that are either expired OR have been collected for more than 10 minutes.
	 * The 10-minute grace period prevents race conditions with other event listeners.
	 *
	 * @param currentTime The current time to compare against for expiry.
	 * @param collectedTimeThreshold The timestamp representing 10 minutes ago.
	 * @return A list of food listings to be cleaned up.
	 */
	@Query(
			"SELECT fl FROM FoodListing fl WHERE fl.expiryDateTime < :currentTime OR (fl.isCollected = true AND fl.updatedAt < :collectedTimeThreshold)"
			)
	List<FoodListing> findListingsForCleanup(LocalDateTime currentTime, LocalDateTime collectedTimeThreshold);
}