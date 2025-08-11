package com.app.common.dto;

/**
 * An enum representing the possible actions a Food Receiver can take on a food listing alert.
 */
public enum ResponseType {
    /**
     * The receiver accepts the food donation.
     */
    ACCEPT,
    /**
     * The receiver rejects the food donation.
     */
    REJECT,
    /**
     * The receiver asks to be reminded about the donation at a later time.
     */
    REMIND_LATER
}