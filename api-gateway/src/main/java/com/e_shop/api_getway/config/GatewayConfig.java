package com.e_shop.api_getway.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


@EnableAutoConfiguration
public class GatewayConfig {
    @Bean
    public GlobalFilter customGlobalFilter() {
        return (exchange, chain) -> {
            // Example: Add custom headers for every request
            exchange.getResponse().getHeaders().add("X-Security-Header", "Enabled");

            // Example: Authentication check
            String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
            if (authHeader == null) {
                return onError(exchange, "Authentication failed", HttpStatus.UNAUTHORIZED);
            }

            return chain.filter(exchange);
        };
    }
    private Mono<Void> onError(ServerWebExchange exchange, String error, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        DataBuffer buffer = response.bufferFactory().wrap(error.getBytes());
        return response.writeWith(Mono.just(buffer));
    }
}
