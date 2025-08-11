package com.app.food.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.food.service.DonorService;

import lombok.RequiredArgsConstructor;

/**
 * REST Controller for donor-specific actions, like finding nearby receivers.
 *
 * @author Radhakrishnan
 * @version 1.0
 */
@RestController
@RequestMapping("/api/donors")
@RequiredArgsConstructor
public class DonorController {

    private final DonorService donorService;

    /**
     * API endpoint to initiate a search for nearby food receivers.
     *
     * @param donorId The ID of the donor making the request (from security context).
     * @return An HTTP 202 Accepted response, indicating the search has started.
     */
    @GetMapping("/nearby-receivers")
    public ResponseEntity<Void> findNearbyReceivers(@RequestHeader("X-User-Id") Long donorId) {
        donorService.initiateFindNearbyReceivers(donorId);
        return ResponseEntity.accepted().build();
    }
}