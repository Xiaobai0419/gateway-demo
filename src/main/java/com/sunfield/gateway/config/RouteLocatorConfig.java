package com.sunfield.gateway.config;

import com.sunfield.gateway.filter.CustomFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteLocatorConfig {

    @Bean
    public RouteLocator customerRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(r -> r.path("/test/prefix/**")
                        .filters(f -> f.stripPrefix(2)
                                .filter(new CustomFilter())
                                .addResponseHeader("X-Response-test", "test"))
                        .uri("lb://SC-CONSUMER")
                        .order(0)
                        .id("test_consumer_service")
                )
                .build();
    }
}
