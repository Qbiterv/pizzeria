package pl.auctane.order.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import pl.auctane.order.dtos.order.OrderDto;
import pl.auctane.order.dtos.order.ProductDto;
import pl.auctane.order.entities.Order;
import pl.auctane.order.services.OrderProductService;
import pl.auctane.order.services.OrderService;
import pl.auctane.order.services.OrderStatusService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/order")
public class OrderController {
    private final OrderService orderService;
    private final ObjectMapper objectMapper;
    private final OrderProductService orderProductService;
    private final OrderStatusService orderStatusService;

    @Autowired
    public OrderController(OrderService orderService, ObjectMapper objectMapper, OrderProductService orderProductService, OrderStatusService orderStatusService) {
        this.orderService = orderService;
        this.objectMapper = objectMapper;
        this.orderProductService = orderProductService;
        this.orderStatusService = orderStatusService;
    }

    @GetMapping(value = "/get")
    public ResponseEntity<?> getOrders() {
        return ResponseEntity.ok().body(orderService.getOrders());
    }

    @GetMapping(value = "/get/{id}")
    public ResponseEntity<?> getOrder(@PathVariable Long id) {
        Optional<Order> order = orderService.getOrderById(id);

        if(order.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok().body(order.get());
    }

    // Initialize service url from application.properties
    @Value("${service.url}")
    private String serviceUrl;

    @PostMapping(value = "/create", consumes =  {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> createOrder(@Valid @RequestBody OrderDto orderDto, BindingResult bindingResult) {
        ObjectNode JSON = objectMapper.createObjectNode();

        // Bean validation
        if (bindingResult.hasErrors()) {
            JSON.put("success", false);
            JSON.put("message", bindingResult.getAllErrors().stream().map(e -> ((FieldError) e).getField() + " " + e.getDefaultMessage()).collect(Collectors.joining(", ")));

            return ResponseEntity.badRequest().body(JSON);
        }

        // Initialize list of products ids
        List<Long> products = new ArrayList<>();

        // Check if all products exist
        for (Long product : orderDto.getProducts()) {
            String url = serviceUrl + "/product/get/" + product;

            System.out.println(url);

            ResponseEntity<ProductDto> response = null;

            try {
                response = new RestTemplate().getForEntity(url, ProductDto.class);
                System.out.println(response.getBody());
            } catch (HttpStatusCodeException | ResourceAccessException e) {
                JSON.put("success", false);
                JSON.put("message", e.getMessage());

                return  ResponseEntity.badRequest().body(JSON);
            }

            ProductDto productDto = response.getBody();

            System.out.println(response.getStatusCode());

            if (productDto == null) {
                JSON.put("success", false);
                JSON.put("message", "Invalid product with id " + product);

                products.clear();
                return ResponseEntity.badRequest().body(JSON);
            }

            if (!response.getStatusCode().is2xxSuccessful()) {
                JSON.put("success", false);
                JSON.put("message", "Invalid product with id " + product);

                products.clear();
                return ResponseEntity.badRequest().body(JSON);
            }

            products.add(product);
        }

        // Create order
        Order order = orderService.createOrder(orderDto.getName(), orderDto.getSurname(), orderDto.getEmail(), orderDto.getPhone(), orderDto.getAddress());

        // Connect all products with order
        for (Long product : products) {
            orderProductService.createOrderProduct(order, product);
        }

        JSON.put("success", true);
        JSON.put("message", "Created order: " + order.getId());

        return ResponseEntity.ok().body(JSON);
    }
}
