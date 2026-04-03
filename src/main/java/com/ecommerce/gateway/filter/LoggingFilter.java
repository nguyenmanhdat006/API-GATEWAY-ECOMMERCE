package com.ecommerce.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    private static final String REQUEST_ID = "X-Request-ID";
    private static final String START_TIME = "startTime";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // Generate and add request ID
        String requestId = UUID.randomUUID().toString();
        exchange.getRequest().mutate().header(REQUEST_ID, requestId).build();

        ServerHttpRequest request = exchange.getRequest();
        long startTime = System.currentTimeMillis();
        exchange.getAttributes().put(START_TIME, startTime);

        String remoteAddress = request.getRemoteAddress() != null
                ? request.getRemoteAddress().getAddress().getHostAddress()
                : "unknown";

        String userAgent = request.getHeaders().getFirst("User-Agent");
        if (userAgent == null) {
            userAgent = "unknown";
        }

        log.info("[GATEWAY] → {} {} | ID: {} | Remote: {} | User-Agent: {}",
                request.getMethod(),
                request.getPath(),
                requestId,
                remoteAddress,
                userAgent);

        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            ServerHttpResponse response = exchange.getResponse();
            long duration = System.currentTimeMillis() - startTime;

            log.info("[GATEWAY] ← {} {} - {} | Duration: {}ms | ID: {}",
                    request.getMethod(),
                    request.getPath(),
                    response.getStatusCode(),
                    duration,
                    requestId);
        }));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}

