package com.ecommerce.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.gateway.filter.factory.RetryGatewayFilterFactory;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Product Service Route
                .route("product-service", r -> r
                        .path("/api/products/**")
                        .uri("lb://product-service")
                        .filters(f -> f
                                .stripPrefix(1)
                                .retry(retryConfig -> retryConfig
                                        .setRetries(3)
                                        .setMethods("GET", "POST")
                                        .setBackoff(1000, 1.1, 2, false))
                                .circuitBreaker(c -> c.setName("product-circuit-breaker"))))

                // Cart Service Route
                .route("cart-service", r -> r
                        .path("/api/cart/**")
                        .uri("lb://cart-service")
                        .filters(f -> f
                                .stripPrefix(1)
                                .retry(retryConfig -> retryConfig
                                        .setRetries(3)
                                        .setMethods("GET", "POST", "PUT")
                                        .setBackoff(1000, 1.1, 2, false))
                                .circuitBreaker(c -> c.setName("cart-circuit-breaker"))))

                // Order Service Route
                .route("order-service", r -> r
                        .path("/api/orders/**")
                        .uri("lb://order-service")
                        .filters(f -> f
                                .stripPrefix(1)
                                .retry(retryConfig -> retryConfig
                                        .setRetries(3)
                                        .setMethods("GET", "POST", "PUT")
                                        .setBackoff(1000, 1.1, 2, false))
                                .circuitBreaker(c -> c.setName("order-circuit-breaker"))))

                // Payment Service Route
                .route("payment-service", r -> r
                        .path("/api/payments/**")
                        .uri("lb://payment-service")
                        .filters(f -> f
                                .stripPrefix(1)
                                .retry(retryConfig -> retryConfig
                                        .setRetries(3)
                                        .setMethods("GET", "POST", "PUT")
                                        .setBackoff(1000, 1.1, 2, false))
                                .circuitBreaker(c -> c.setName("payment-circuit-breaker"))))

                .build();
    }
}

