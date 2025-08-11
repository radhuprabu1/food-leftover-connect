package com.app.food.service;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.app.common.config.AppUtilConstants;
import com.app.common.config.MessagingConfigConstants;
import com.app.common.dto.LocationDTO;
import com.app.common.event.FindNearbyReceiversRequest;
import com.app.food.model.DonorView;
import com.app.food.repository.DonorViewRepository;

import lombok.RequiredArgsConstructor;

/**
 * Service class for donor-specific business logic.
 *
 * @author Radhakrishnan
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class DonorService {
    private static final Logger log = LoggerFactory.getLogger(DonorService.class);
    private final DonorViewRepository donorViewRepository;
    private final RabbitTemplate rabbitTemplate;

    public void initiateFindNearbyReceivers(Long donorId) {
        // 1. Get the donor's location from our local view.
        DonorView donor = donorViewRepository.findById(donorId)
                .orElseThrow(() -> new IllegalStateException("Donor not found: " + donorId));

        // 2. Create a unique ID to track this request/response pair.
        String correlationId = UUID.randomUUID().toString();

        // 3. Create the request event.
        FindNearbyReceiversRequest requestEvent = new FindNearbyReceiversRequest(
                correlationId,
                new LocationDTO(donor.getLatitude(), donor.getLongitude()),
                AppUtilConstants.SEARCH_RADIUS_KM // The 5km radius as requested
        );

        // 4. Publish the request to the main food events exchange for the receiver-service to pick up.
        rabbitTemplate.convertAndSend(MessagingConfigConstants.FOOD_EVENTS_EXCHANGE, "", requestEvent);
        log.info("Published FindNearbyReceiversRequest with correlation ID: {}", correlationId);
    }
}