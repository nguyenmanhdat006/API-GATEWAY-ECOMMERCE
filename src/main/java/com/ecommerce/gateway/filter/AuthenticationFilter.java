package com.ecommerce.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class AuthenticationFilter implements GlobalFilter, Ordered {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    // Public endpoints that don't require authentication
    private static final String[] PUBLIC_PATHS = {
            "/api/products",
            "/api/auth/login",
            "/api/auth/register",
            "/actuator/gateway/routes",
            "/actuator/health"
    };

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().value();

        // Skip authentication for public endpoints
        if (isPublicEndpoint(path)) {
            log.debug("Public endpoint accessed: {}", path);
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(AUTHORIZATION_HEADER);

        if (authHeader == null || authHeader.isEmpty()) {
            log.warn("Missing authorization header for path: {}", path);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        if (!authHeader.startsWith(BEARER_PREFIX)) {
            log.warn("Invalid authorization header format for path: {}", path);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(BEARER_PREFIX.length());

        // TODO: Validate token with Keycloak or Identity Service
        // Steps for JWT validation:
        // 1. Decode JWT
        // 2. Verify signature
        // 3. Check expiration
        // 4. Extract user info (user-id, roles)
        // 5. Add to headers for downstream services
        
        // For now, we just pass it through
        // Backend services will validate

        log.debug("Token extracted and validated for path: {}", path);

        return chain.filter(exchange);
    }

    private boolean isPublicEndpoint(String path) {
        for (String publicPath : PUBLIC_PATHS) {
            if (path.startsWith(publicPath)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getOrder() {
        // Run after LoggingFilter (HIGHEST_PRECEDENCE)
        // but before other filters
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }
}

