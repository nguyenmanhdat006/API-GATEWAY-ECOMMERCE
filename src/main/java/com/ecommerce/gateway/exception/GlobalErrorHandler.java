package com.ecommerce.gateway.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
@Order(-2)
@RequiredArgsConstructor
@Slf4j
public class GlobalErrorHandler implements ErrorWebExceptionHandler {
    
    private final ObjectMapper objectMapper;
    
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        log.error("Gateway error occurred: ", ex);
        
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = "Internal Gateway Error";
        
        // Determine status and message based on exception type
        if (ex instanceof ResponseStatusException) {
            ResponseStatusException rse = (ResponseStatusException) ex;
            status = HttpStatus.valueOf(rse.getStatusCode().value());
            message = rse.getReason() != null ? rse.getReason() : status.getReasonPhrase();
        } else if (ex.getMessage() != null) {
            if (ex.getMessage().contains("Connection refused") || 
                ex.getMessage().contains("Connection reset")) {
                status = HttpStatus.SERVICE_UNAVAILABLE;
                message = "Service temporarily unavailable";
            } else if (ex.getMessage().contains("Timeout") || 
                       ex.getMessage().contains("timeout")) {
                status = HttpStatus.GATEWAY_TIMEOUT;
                message = "Request timeout - service took too long to respond";
            } else if (ex.getMessage().contains("404") || 
                       ex.getMessage().contains("Not Found")) {
                status = HttpStatus.NOT_FOUND;
                message = "Requested resource not found";
            } else if (ex.getMessage().contains("429") || 
                       ex.getMessage().contains("Too Many Requests")) {
                status = HttpStatus.TOO_MANY_REQUESTS;
                message = "Rate limit exceeded - too many requests";
            }
        }
        
        // Build error response
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("message", message);
        errorResponse.put("error_type", ex.getClass().getSimpleName());
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        errorResponse.put("path", exchange.getRequest().getPath().value());
        errorResponse.put("status", status.value());
        
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        
        try {
            byte[] bytes = objectMapper.writeValueAsBytes(errorResponse);
            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
            return exchange.getResponse().writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            log.error("Error writing error response", e);
            return exchange.getResponse().setComplete();
        }
    }
}


