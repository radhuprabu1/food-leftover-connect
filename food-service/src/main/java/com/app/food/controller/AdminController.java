package com.app.food.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.food.model.DonorView;
import com.app.food.model.FoodListing;
import com.app.food.repository.DonorViewRepository;
import com.app.food.repository.FoodListingRepository;

import lombok.RequiredArgsConstructor;

/**
 * REST Controller for admin-only endpoints related to the donor domain.
 *
 * @author Radhakrishnan
 * @version 1.0
 */
@RestController("foodAdminController")
@RequestMapping("/api/admin/food-listings")
@RequiredArgsConstructor
public class AdminController {

    private final FoodListingRepository foodListingRepository;
    private final DonorViewRepository donorViewRepository;

    /**
     * Retrieves all food listings in the system.
     * In a secure context, this endpoint would be restricted to users with ROLE_ADMIN.
     *
     * @return A list of all food listings.
     */
    @GetMapping
    public ResponseEntity<List<FoodListing>> getAllFoodListings() {
        return ResponseEntity.ok(foodListingRepository.findAll());
    }
    
    /**
     * Retrieves all donor views stored locally in this service.
     * This is useful for admins to see which donors are synchronized.
     *
     * @return A list of all donor views.
     */
    @GetMapping("/views")
    public ResponseEntity<List<DonorView>> getAllDonorViews() {
        return ResponseEntity.ok(donorViewRepository.findAll());
    }
    
    
}