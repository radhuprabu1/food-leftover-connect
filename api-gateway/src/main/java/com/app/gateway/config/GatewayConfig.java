package com.app.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for the API Gateway routes only.
 * This class simply defines where incoming requests should be forwarded.
 *
 * @author Radhakrishnan
 * @version 1.0
 */
@Configuration
public class GatewayConfig {

    /**
     * Defines the routing rules for the gateway.
     *
     * @param builder The builder for creating route locators.
     * @return The configured RouteLocator.
     */
    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("services_route", r -> r.path("/api/**", "/h2-console/**")
                        .uri("http://localhost:8081"))
                .build();
    }
}