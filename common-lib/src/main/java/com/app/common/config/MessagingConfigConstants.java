package com.app.common.config;

/**
 * A centralized place to hold all RabbitMQ messaging-related constants.
 * This ensures that producers and consumers use the exact same names for exchanges, queues, and routing keys.
 *
 * @author Radhakrishnan
 * @version 1.0
 */
public class MessagingConfigConstants {

    // --- Exchanges for Domain Events ---

    /**
     * Fanout exchange for broadcasting events related to the user lifecycle.
     * e.g., UserRegisteredEvent, UserUpdatedEvent.
     */
    public static final String USER_EVENTS_EXCHANGE = "user.events.exchange";

    /**
     * Fanout exchange for broadcasting events related to the food listing lifecycle.
     * e.g., FoodListedEvent, FoodAcceptedEvent.
     */
    public static final String FOOD_EVENTS_EXCHANGE = "food.events.exchange";


    // --- Exchange for Targeted Commands ---

    /**
     * Direct exchange for sending targeted commands, like notifications to a specific user.
     * Messages sent here require a specific routing key.
     */
    public static final String NOTIFICATIONS_DIRECT_EXCHANGE = "notifications.direct.exchange";


    // --- Routing Keys for Commands ---

    /**
     * Routing key used by the receiver-service to command the notification-service
     * to send a real-time alert about a new food listing to a specific, matched receiver.
     */
    public static final String FOOD_ALERT_ROUTING_KEY = "food.alert.notify";

}