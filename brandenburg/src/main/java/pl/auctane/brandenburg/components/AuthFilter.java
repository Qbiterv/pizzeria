package pl.auctane.brandenburg.components;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Component
public class AuthFilter implements GatewayFilter {

    @Value("${authentication.enabled}")
    private boolean authenticationEnabled;
    @Value("${authentication.token}")
    private String token;

    @Autowired
    public AuthFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    final ObjectMapper objectMapper;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if(!authenticationEnabled) {
            System.out.println("Authentication is disabled. To enable it, make \"authentication.enabled\" property as true");
            return chain.filter(exchange);
        }

        ServerHttpRequest request = exchange.getRequest();

        if(!hasAllCredentials(request)){
            return sendBack(exchange,"Credentials missing");
        }

        //check token
        if(!Objects.equals(request.getHeaders().getFirst("Token"), token)){
            return sendBack(exchange,"Invalid token");
        }

        return chain.filter(exchange);
    }

    private boolean hasAllCredentials(ServerHttpRequest request) {
        return request.getHeaders().containsKey("Token") && request.getHeaders().containsKey("Role");
    }

    private Mono<Void> sendBack(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        ObjectNode JSON = objectMapper.createObjectNode();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, "application/json");

        JSON.put("success", false);
        JSON.put("message", message);

        DataBuffer dataBuffer = response.bufferFactory().wrap(JSON.toString().getBytes());

        return response.writeWith(Mono.just(dataBuffer));
    }
}
