package com.app.common.dto;

import java.time.LocalDateTime;

/**
 * A Data Transfer Object representing a food receiver's response to a food listing alert.
 *
 * @param responseType The type of response (ACCEPT, REJECT, REMIND_LATER).
 * @param reminderTime An optional timestamp for when to be reminded if the response is REMIND_LATER.
 */
public record ReceiverResponseDTO(
    ResponseType responseType,
    LocalDateTime reminderTime
) {}