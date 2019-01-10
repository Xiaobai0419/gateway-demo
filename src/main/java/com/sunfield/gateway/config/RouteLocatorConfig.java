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
                        .filters(f -> f.stripPrefix(2)//r.path(“/test/prefix/**”)表示自定义了访问前缀，在真正的Gateway进行路由转发的时候，会用过f.stripPrefix(2)把前缀去掉。
                                .filter(new CustomFilter())
                                .addResponseHeader("X-Response-test", "test"))
                        .uri("lb://SC-CONSUMER")//基于服务发现的服务名访问，注意协议类型为lb://而不是http://（无法路由成功），且gateway默认大写路由服务名
                        .order(0)
                        .id("test_consumer_service")
                )
                .build();
    }
}
