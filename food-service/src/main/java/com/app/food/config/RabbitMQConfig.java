package com.app.food.config;

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
 * Configures RabbitMQ components for the donor-service.
 * <p>
 * This class declares the queues this service listens to and binds them to the
 * appropriate exchanges for receiving domain events.
 *
 * @author Radhakrishnan
 * @version 1.0
 */
@Configuration("donorRabbitMQConfig")
public class RabbitMQConfig {

	/**
	 * The name of the queue this service uses to listen for relevant events.
	 */
	public static final String DONOR_UPDATES_QUEUE = "donor.updates.queue";

	// --- Exchange Declarations ---

	/**
	 * Declares the fanout exchange for food-related events.
	 * This service is a primary publisher to this exchange.
	 *
	 * @return The configured Exchange bean for food events.
	 */
	@Bean(name = "donorFoodEventsExchange")
	public Exchange foodEventsExchange() {
		return ExchangeBuilder.fanoutExchange(MessagingConfigConstants.FOOD_EVENTS_EXCHANGE).durable(true).build();
	}

	/**
	 * Declares the fanout exchange for user-related events to ensure it exists.
	 * This service listens to this exchange for updates on donors.
	 *
	 * @return The configured Exchange bean for user events.
	 */
	@Bean(name = "donorUserEventsExchange")
	public Exchange userEventsExchange() {
		return ExchangeBuilder.fanoutExchange(MessagingConfigConstants.USER_EVENTS_EXCHANGE).durable(true).build();
	}

	// --- Queue Declaration ---

	/**
	 * Declares the single, durable queue for this service.
	 *
	 * @return The configured Queue bean.
	 */
    @Bean(name = "donorUpdatesQueue")
	public Queue donorUpdatesQueue() {
		return QueueBuilder.durable(DONOR_UPDATES_QUEUE).build();
	}

	// --- Bindings ---

	/**
	 * Binds the service's queue to the food events exchange.
	 * This allows the service to listen for events like {@link com.app.common.event.FoodAcceptedEvent}.
	 *
	 * @param queue    the service's queue.
	 * @param exchange the food events exchange.
	 * @return The configured Binding.
	 */
    @Bean(name = "donorFoodUpdatesBinding")
	public Binding foodUpdatesBinding(@Qualifier("donorUpdatesQueue") Queue queue, @Qualifier("donorFoodEventsExchange") Exchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with("").noargs();
	}

	/**
	 * Binds the service's queue to the user events exchange.
	 * This allows the service to listen for {@link com.app.common.event.UserRegisteredEvent}
	 * to build its local view of donors.
	 *
	 * @param queue    the service's queue.
	 * @param exchange the user events exchange.
	 * @return The configured Binding.
	 */
	@Bean(name = "donorUserUpdatesBinding")
	public Binding userUpdatesBinding(@Qualifier("donorUpdatesQueue") Queue queue, @Qualifier("donorUserEventsExchange") Exchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with("").noargs();
	}
}