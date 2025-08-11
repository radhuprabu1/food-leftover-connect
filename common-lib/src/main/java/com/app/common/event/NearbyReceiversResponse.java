package com.app.common.event;

import com.app.common.dto.ReceiverDTO;
import java.util.List;

/**
 * An event published in response to a FindNearbyReceiversRequest, containing
 * the list of matched receivers.
 *
 * @param correlationId The ID that links this response to the original request.
 * @param receivers     The list of nearby receivers found.
 */
public record NearbyReceiversResponse(
    String correlationId,
    List<ReceiverDTO> receivers
) {}