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
import pl.auctane.brandenburg.services.SessionService;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Component
public class AuthFilter implements GatewayFilter {

    @Value("${authentication.enabled}")
    private boolean authenticationEnabled;

    public AuthFilter(ObjectMapper objectMapper, SessionService sessionService) {
        this.objectMapper = objectMapper;
        this.sessionService = sessionService;
    }

    final ObjectMapper objectMapper;
    final SessionService sessionService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // Allow preflight requests to pass through
//        if ("OPTIONS".equalsIgnoreCase(request.getMethod().name()))
//            return chain.filter(exchange);

        if (!hasAllCredentials(request))
            return sendBack(exchange, "Credentials missing");

        if (authenticationEnabled) {
            boolean hasUserAccess = false;
            try {
                hasUserAccess = hasUserAccess(request);
            } catch (Exception e) {
                //session expired
                return sendBack(exchange, e.getMessage());
            }

            if (hasUserAccess)
                return chain.filter(exchange);

            return sendBack(exchange, "Unauthorized");
        }

        return chain.filter(exchange);
    }


    private boolean hasAllCredentials(ServerHttpRequest request) {
        return request.getHeaders().containsKey("Authorization");
    }
    private boolean hasUserAccess(ServerHttpRequest request) throws Exception {
        String authorization = request.getHeaders().getFirst("Authorization");
        return sessionService.hasUserAccess(authorization, request);
    }

    private Mono<Void> sendBack(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        ObjectNode JSON = objectMapper.createObjectNode();

        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, "application/json");

        // Add CORS headers
        response.getHeaders().add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        response.getHeaders().add(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "Origin, X-Requested-With, Content-Type, Accept, Token, Role");
        response.getHeaders().add(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET, POST, PUT, DELETE, OPTIONS");
        response.getHeaders().add(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");

        JSON.put("success", false);
        JSON.put("message", message);

        DataBuffer dataBuffer = response.bufferFactory().wrap(JSON.toString().getBytes());

        return response.writeWith(Mono.just(dataBuffer));
    }
}