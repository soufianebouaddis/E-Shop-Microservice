package com.e_shop.api_getway.config;

import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.cloud.gateway.discovery.DiscoveryClientRouteDefinitionLocator;
import org.springframework.cloud.gateway.discovery.DiscoveryLocatorProperties;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DynamicRoutes {
    @Bean
    DiscoveryClientRouteDefinitionLocator routesDynamique(
            ReactiveDiscoveryClient rdc,
            DiscoveryLocatorProperties dlp) {
        dlp.setEnabled(true);
        dlp.setLowerCaseServiceId(true);
        return new DiscoveryClientRouteDefinitionLocator(rdc, dlp);
    }
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-service", r -> r
                        .path("/api/v1/auth/**")
                        .uri("lb://AUTH-SERVICE"))
                .route("product-service", r -> r
                        .path("/api/v1/product/**")
                        .uri("lb://PRODUCT-SERVICE"))
                .build();
    }
    @Bean
    public GlobalFilter customGlobalFilter() {
        return (exchange, chain) -> {
            exchange.getResponse().getHeaders().add("X-Security-Header", "Enabled");
            return chain.filter(exchange);
        };
    }
}
