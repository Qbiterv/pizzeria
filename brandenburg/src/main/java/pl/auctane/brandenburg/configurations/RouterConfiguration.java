package pl.auctane.brandenburg.configurations;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.auctane.brandenburg.components.AuthFilter;
import pl.auctane.brandenburg.services.SessionService;

@Configuration
public class RouterConfiguration {

    @Value("${service.version}")
    private String serviceVersion;
    @Value("${service.order.url}")
    private String serviceOrderUrl;
    @Value("${service.meal.url}")
    private String serviceMealUrl;

    @Bean
    public AuthFilter authFilter(ObjectMapper objectMapper, SessionService sessionService) {
        return new AuthFilter(objectMapper, sessionService);
    }

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder, AuthFilter authFilter) {
        return builder.routes()
                .route("order", r -> r.path("/order")
                        .and().method("POST").filters(f -> f.cacheRequestBody(String.class).rewritePath("/order", "/"+serviceVersion+"/order/create")
                                .filters())
                        .uri(serviceOrderUrl))

                .route("products", r -> r.path("/products")
                        .and().method("GET").filters(f -> f.rewritePath("/products", "/"+serviceVersion+"/product/get")
                                .filters(authFilter))
                        .uri(serviceMealUrl))

                .route("product", r -> r.path("/product/{id}")
                        .and().method("GET").filters(f -> f.rewritePath("/product/(?<id>.*)", "/"+serviceVersion+"/product/get/${id}")
                                .filters(authFilter))
                        .uri(serviceMealUrl))

                .route("meals", r -> r.path("/meals/{id}")
                        .and().method("GET").filters(f -> f.rewritePath("/meals/(?<id>.*)", "/"+serviceVersion+"/product-meal/product/${id}")
                                .filters())
                        .uri(serviceMealUrl))

                .build();

//                .route("user", r -> r.path("/user/**")
//                        .uri("http://localhost:8082"))
//                .build();
    }
}
