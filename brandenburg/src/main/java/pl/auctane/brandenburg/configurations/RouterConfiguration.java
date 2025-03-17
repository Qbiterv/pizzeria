package pl.auctane.brandenburg.configurations;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

public class RouterConfiguration {

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("mail", r -> r.path("/mail/**")
                        .uri("http://localhost:8081"))
                .route("user", r -> r.path("/user/**")
                        .uri("http://localhost:8082"))
                .build();
    }
}
