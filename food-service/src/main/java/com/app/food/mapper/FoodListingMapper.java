package com.app.food.mapper;

import com.app.common.dto.DonorDTO;
import com.app.common.dto.FoodCreationRequest;
import com.app.common.dto.LocationDTO;
import com.app.common.event.FoodListedEvent;
import com.app.food.model.DonorView;
import com.app.food.model.FoodListing;

/**
 * A utility class for mapping between food listing DTOs, entities, and events.
 *
 * @author Radhakrishnan
 * @version 1.1
 */
public final class FoodListingMapper {

	private FoodListingMapper() {}

	/**
	 * Maps a {@link FoodCreationRequest} DTO and a donor's ID to a new {@link FoodListing} entity.
	 *
	 * @param request The API request data for the new food listing.
	 * @param donorId The ID of the donor creating the listing.
	 * @return A new {@link FoodListing} entity, ready to be persisted.
	 */
	public static FoodListing toEntity(FoodCreationRequest request, Long donorId) {
		FoodListing foodListing = new FoodListing();
		foodListing.setDonorId(donorId);
		foodListing.setFoodName(request.foodName());
		foodListing.setPreparedDateTime(request.preparedDateTime());
		foodListing.setQuantity(request.quantity());
		foodListing.setExpiryDateTime(request.expiryDateTime());
		foodListing.setPickupTime(request.pickupTime());
		return foodListing;
	}

	/**
	 * Maps a saved {@link FoodListing} entity and a {@link DonorView} to a {@link FoodListedEvent}.
	 *
	 * @param savedFoodListing The food listing entity after it has been saved to the database.
	 * @param donor            The local view of the donor who created the listing.
	 * @return A new {@link FoodListedEvent} instance, ready to be published.
	 */
	public static FoodListedEvent toFoodListedEvent(FoodListing savedFoodListing, DonorView donor) {
		DonorDTO donorDTO = new DonorDTO(
				donor.getId(),
				donor.getName(),
				new LocationDTO(donor.getLatitude(), donor.getLongitude())
				);

		return new FoodListedEvent(
				savedFoodListing.getId(),
				savedFoodListing.getFoodName(),
				savedFoodListing.getQuantity(),
				donorDTO,
				savedFoodListing.getExpiryDateTime()
				);
	}
}