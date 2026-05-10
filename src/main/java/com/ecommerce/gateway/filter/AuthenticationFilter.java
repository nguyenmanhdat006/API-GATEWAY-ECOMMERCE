package com.ecommerce.gateway.filter;

import com.ecommerce.gateway.config.GatewayProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.core.io.buffer.DataBuffer;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationFilter implements GlobalFilter, Ordered {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final GatewayProperties gatewayProperties;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().value();

        if (isPublicPath(path)) {
            log.debug("[AUTH-FILTER] Public path, skipping auth check: {}", path);
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(AUTHORIZATION_HEADER);

        if (authHeader == null || authHeader.isBlank()) {
            log.warn("[AUTH-FILTER] Missing Authorization header for: {}", path);
            return unauthorizedResponse(exchange, "Missing Authorization header");
        }

        if (!authHeader.startsWith(BEARER_PREFIX)) {
            log.warn("[AUTH-FILTER] Invalid Authorization format for: {}", path);
            return unauthorizedResponse(exchange, "Authorization header must start with 'Bearer '");
        }

        String token = authHeader.substring(BEARER_PREFIX.length()).trim();

        if (token.isBlank()) {
            log.warn("[AUTH-FILTER] Empty Bearer token for: {}", path);
            return unauthorizedResponse(exchange, "Bearer token must not be empty");
        }

        log.debug("[AUTH-FILTER] Token present, forwarding to downstream: {}", path);
        return chain.filter(exchange);
    }

    private boolean isPublicPath(String path) {
        return gatewayProperties.getPublicPaths().stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String body = String.format(
                "{\"success\":false,\"status\":401,\"message\":\"%s\"}",
                message.replace("\"", "\\\""));

        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }
}
