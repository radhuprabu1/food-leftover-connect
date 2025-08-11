package com.app.notification.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.app.common.config.MessagingConfigConstants;

/**
 * Configures RabbitMQ components for the notification-service.
 *
 * @author Radhakrishnan
 * @version 1.0
 */
@Configuration("notificationRabbitMQConfig")
public class RabbitMQConfig {

	public static final String NOTIFICATIONS_QUEUE = "notifications.queue";

	// --- Exchange Declarations ---
	@Bean(name = "notificationFoodEventsExchange")
	public Exchange foodEventsExchange() {
		return ExchangeBuilder.fanoutExchange(MessagingConfigConstants.FOOD_EVENTS_EXCHANGE).durable(true).build();
	}
	@Bean(name = "notificationUserEventsExchange")
	public Exchange userEventsExchange() {
		return ExchangeBuilder.fanoutExchange(MessagingConfigConstants.USER_EVENTS_EXCHANGE).durable(true).build();
	}

    @Bean(name = "notificationDirectExchange")
	public Exchange notificationsDirectExchange() {
		return ExchangeBuilder.directExchange(MessagingConfigConstants.NOTIFICATIONS_DIRECT_EXCHANGE).durable(true).build();
	}

	// --- Queue Declaration ---
    @Bean(name = "notificationsQueue")
	public Queue notificationsQueue() {
		return QueueBuilder.durable(NOTIFICATIONS_QUEUE).build();
	}

	// --- Bindings ---
	 @Bean(name = "notificationFoodEventsBinding")
	 public Binding foodEventsBinding(@Qualifier("notificationsQueue") Queue queue, @Qualifier("notificationFoodEventsExchange") Exchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with("").noargs();
	}
	@Bean(name = "notificationUserEventsBinding")
	public Binding userEventsBinding(@Qualifier("notificationsQueue") Queue queue, @Qualifier("notificationUserEventsExchange") Exchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with("").noargs();
	}
	
    @Bean(name = "notificationDirectBinding")
	public Binding directBinding(@Qualifier("notificationsQueue") Queue queue, @Qualifier("notificationDirectExchange") Exchange exchange) {
		return BindingBuilder.bind(queue)
				.to(exchange)
				.with(MessagingConfigConstants.FOOD_ALERT_ROUTING_KEY)
				.noargs();
	}
}