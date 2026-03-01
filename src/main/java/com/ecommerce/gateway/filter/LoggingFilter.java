package com.ecommerce.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
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
    public Mono<Void> filter(ServerWebExchange exchange, org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {
        // Generate and add request ID
        String requestId = UUID.randomUUID().toString();
        exchange.getRequest().mutate().header(REQUEST_ID, requestId).build();

        long startTime = System.currentTimeMillis();
        exchange.getAttributes().put(START_TIME, startTime);

        log.info("[REQUEST] ID: {}, Method: {}, Path: {}, Remote Address: {}",
                requestId,
                exchange.getRequest().getMethod(),
                exchange.getRequest().getPath(),
                exchange.getRequest().getRemoteAddress());

        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            log.info("[RESPONSE] ID: {}, Status: {}, Duration: {}ms",
                    requestId,
                    exchange.getResponse().getStatusCode(),
                    duration);
        }));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}

