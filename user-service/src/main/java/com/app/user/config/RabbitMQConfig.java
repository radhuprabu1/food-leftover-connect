package com.app.user.config;

import com.app.common.config.MessagingConfigConstants;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configures RabbitMQ components specific to the user-service.
 * <p>
 * This class is responsible for declaring the exchanges that this service publishes to.
 * By giving the @Configuration annotation a name, we ensure this bean has a unique
 * identifier ("userRabbitMQConfig"), preventing conflicts with other RabbitMQConfig
 * classes in different modules during component scanning.
 *
 * @author Radhakrishnan
 * @version 1.0
 */
@Configuration("userRabbitMQConfig")
public class RabbitMQConfig {

	/**
	 * Defines the fanout exchange for user-related domain events.
	 * <p>
	 * A fanout exchange broadcasts all messages it receives to all queues that are bound to it.
	 * This is ideal for domain events like 'UserRegisteredEvent' which multiple services
	 * (donor-service, receiver-service, notification-service) might need to know about.
	 * The exchange is declared as 'durable' to ensure it survives a broker restart.
	 *
	 * @return The configured Exchange bean for user events.
	 */
	@Bean(name = "userUserEventsExchange")
	public Exchange userEventsExchange() {
		return ExchangeBuilder.fanoutExchange(MessagingConfigConstants.USER_EVENTS_EXCHANGE)
				.durable(true)
				.build();
	}
}