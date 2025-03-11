package pl.auctane.order.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import pl.auctane.order.dtos.order.OrderDto;
import pl.auctane.order.dtos.order.ProductDto;
import pl.auctane.order.entities.Order;
import pl.auctane.order.entities.OrderProduct;
import pl.auctane.order.services.OrderProductService;
import pl.auctane.order.services.OrderService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/v1/order")
public class OrderController {
    private final OrderService orderService;
    private final ObjectMapper objectMapper;
    private final OrderProductService orderProductService;

    @Autowired
    public OrderController(OrderService orderService, ObjectMapper objectMapper, OrderProductService orderProductService) {
        this.orderService = orderService;
        this.objectMapper = objectMapper;
        this.orderProductService = orderProductService;
    }

    @GetMapping(value = "/get", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getOrders() {
        return ResponseEntity.ok().body(orderService.getOrders());
    }

    @GetMapping(value = "/get/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getOrder(@PathVariable Long id) {
        Optional<Order> order = orderService.getOrderById(id);

        if(order.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok().body(order.get());
    }

    @Value("${service.url}")
    private String serviceUrl;

    @PostMapping(value = "/create", consumes =  {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> createOrder(@RequestBody OrderDto orderDto) {
        ObjectNode JSON = objectMapper.createObjectNode();

        if(orderDto == null ) {
            JSON.put("success", false);
            JSON.put("message", "Order DTO is null");

            return ResponseEntity.badRequest().body(JSON);
        }

        if(orderDto.getName() == null || orderDto.getName().isEmpty()) {
            JSON.put("success", false);
            JSON.put("message", "Name is empty");

            return ResponseEntity.badRequest().body(JSON);
        }

        if(orderDto.getSurname() == null || orderDto.getSurname().isEmpty()) {
            JSON.put("success", false);
            JSON.put("message", "Surname is empty");

            return ResponseEntity.badRequest().body(JSON);
        }

        if(orderDto.getEmail() == null || orderDto.getEmail().isEmpty() || !orderDto.getEmail().matches("^((?!\\.)[\\w\\-_.]*[^.])(@\\w+)(\\.\\w+(\\.\\w+)?[^.\\W])$") || orderDto.getEmail().length() > 255) {
            JSON.put("success", false);
            JSON.put("message", "Email is empty or invalid");

            return ResponseEntity.badRequest().body(JSON);
        }

        if(orderDto.getPhone() == null || orderDto.getPhone().isEmpty()) {
            JSON.put("success", false);
            JSON.put("message", "Phone is empty");

            return ResponseEntity.badRequest().body(JSON);
        }

        if(orderDto.getAddress() == null || orderDto.getAddress().isEmpty()) {
            JSON.put("success", false);
            JSON.put("message", "Address is empty");

            return ResponseEntity.badRequest().body(JSON);
        }

        if(orderDto.getProducts() == null || orderDto.getProducts().isEmpty()) {
            JSON.put("success", false);
            JSON.put("message", "Products list is empty");

            return ResponseEntity.badRequest().body(JSON);
        }

        List<Long> products = orderDto.getProducts();

        Order newOrder = new Order();
        newOrder.setName(orderDto.getName());
        newOrder.setSurname(orderDto.getSurname());
        newOrder.setEmail(orderDto.getEmail());
        newOrder.setPhone(orderDto.getPhone());
        newOrder.setAddress(orderDto.getAddress());

        Order order = orderService.createOrder(newOrder);

        for (Long product : products) {
            String url = serviceUrl + "/product/get/" + product;

            System.out.println(url);
            System.out.println(order.getId());

            ResponseEntity<ProductDto> response = null;

            try {
                response = new RestTemplate().getForEntity(url, ProductDto.class);
                System.out.println(response.getBody());
            } catch (HttpStatusCodeException | ResourceAccessException e) {
                JSON.put("success", false);
                JSON.put("message", e.getMessage());

                return  ResponseEntity.badRequest().body(JSON);
            }

            if (response == null) {
                JSON.put("success", false);
                JSON.put("message", "Invalid product with id " + product);

                orderService.removeOrder(order);
                return ResponseEntity.badRequest().body(JSON);
            }

            ProductDto productDto = response.getBody();

            System.out.println(response.getStatusCode());

            if (productDto == null) {
                JSON.put("success", false);
                JSON.put("message", "Invalid product with id " + product);

                orderService.removeOrder(order);
                return ResponseEntity.badRequest().body(JSON);
            }

            if (!response.getStatusCode().is2xxSuccessful()) {
                JSON.put("success", false);
                JSON.put("message", "Invalid product with id " + product);

                orderService.removeOrder(order);
                return ResponseEntity.badRequest().body(JSON);
            }

            orderProductService.createOrderProduct(order, product);
        }

        JSON.put("success", true);
        JSON.put("message", "Created order: " + order.getId());

        return ResponseEntity.ok().build();
    }
}
