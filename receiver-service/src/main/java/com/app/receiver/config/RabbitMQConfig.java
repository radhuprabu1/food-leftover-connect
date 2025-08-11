package com.app.receiver.config;

import com.app.common.config.MessagingConfigConstants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configures RabbitMQ components for the receiver-service.
 * <p>
 * This class declares the queue this service listens to and binds it to the
 * appropriate exchanges for receiving both broad domain events and user-specific events.
 *
 * @author Radhakrishnan
 * @version 1.0
 */
@Configuration("receiverRabbitMQConfig")
public class RabbitMQConfig {

    /**
     * The name of the queue this service uses to listen for relevant events.
     */
    public static final String RECEIVER_EVENTS_QUEUE = "receiver.events.queue";

    // --- Exchange Declarations ---

    /**
     * Declares the fanout exchange for food-related events to ensure it exists.
     *
     * @return The configured Exchange bean for food events.
     */
    @Bean(name = "receiverFoodEventsExchange")
    public Exchange foodEventsExchange() {
        return ExchangeBuilder.fanoutExchange(MessagingConfigConstants.FOOD_EVENTS_EXCHANGE).durable(true).build();
    }

    /**
     * Declares the fanout exchange for user-related events to ensure it exists.
     *
     * @return The configured Exchange bean for user events.
     */
    @Bean(name = "receiverUserEventsExchange")
    public Exchange userEventsExchange() {
        return ExchangeBuilder.fanoutExchange(MessagingConfigConstants.USER_EVENTS_EXCHANGE).durable(true).build();
    }

    // --- Queue Declaration ---

    /**
     * Declares the single, durable queue for this service.
     *
     * @return The configured Queue bean.
     */
    @Bean(name = "receiverEventsQueue")
    public Queue receiverEventsQueue() {
        return QueueBuilder.durable(RECEIVER_EVENTS_QUEUE).build();
    }

    // --- Bindings ---

    /**
     * Binds the service's queue to the food events exchange.
     * This allows the service to listen for {@link com.app.common.event.FoodListedEvent}.
     *
     * @param queue    the service's queue.
     * @param exchange the food events exchange.
     * @return The configured Binding.
     */
    @Bean(name = "receiverFoodEventsBinding")
    public Binding foodEventsBinding(@Qualifier("receiverEventsQueue") Queue queue, @Qualifier("receiverFoodEventsExchange") Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("").noargs();
    }

    /**
     * Binds the service's queue to the user events exchange.
     * This allows the service to listen for {@link com.app.common.event.UserRegisteredEvent}.
     *
     * @param queue    the service's queue.
     * @param exchange the user events exchange.
     * @return The configured Binding.
     */
    @Bean(name = "receiverUserEventsBinding")
    public Binding userEventsBinding(@Qualifier("receiverEventsQueue") Queue queue, @Qualifier("receiverUserEventsExchange") Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("").noargs();
    }
}