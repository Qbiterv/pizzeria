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
import pl.auctane.order.dtos.order.MailPayloadDto;
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

    @Value("${service.mail.url}")
    private String mailServiceUrl;

    @Autowired
    public OrderController(OrderService orderService, ObjectMapper objectMapper, OrderProductService orderProductService, OrderStatusService orderStatusService) {
        this.orderService = orderService;
        this.objectMapper = objectMapper;
        this.orderProductService = orderProductService;
        this.orderStatusService = orderStatusService;
    }

    @GetMapping(value = "/get")
    public ResponseEntity<?> getOrders() {
        return ResponseEntity.ok().body(orderService.getOrdersSorted());
    }

    @GetMapping("/get/finalized={finalized}")
    public ResponseEntity<?> getOrdersByStatus(@PathVariable("finalized") boolean finalized) {
        if(finalized) {
            return ResponseEntity.ok().body(orderService.getFinalizedOrders());
        }

        if (!finalized) {
            return ResponseEntity.ok().body(orderService.getNotFinalizedOrders());
        }

        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/get/email={email}")
    public ResponseEntity<?> getOrdersByEmail(@PathVariable("email") String email) {
        List<Order> orders = orderService.getOrdersByEmail(email);

        if (orders.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok().body(orders);
    }

    @PutMapping("/set-finalized/{id}")
    public ResponseEntity<?> setFinalized(@PathVariable("id") Long id) {
        ObjectNode JSON = objectMapper.createObjectNode();
        Optional<Order> order = orderService.getOrderById(id);

        if(order.isEmpty()) {
            JSON.put("success", false);
            JSON.put("message", "Order with id " + id + " does not exist");

            return ResponseEntity.badRequest().body(JSON);
        }

        if(order.get().isFinalized()) {
            JSON.put("success", false);
            JSON.put("message", "Order with id " + id + " is already finalized");

            return ResponseEntity.badRequest().body(JSON);
        }

        orderService.setFinalized(order.get());

        JSON.put("success", true);
        JSON.put("message", "Order with id " + id + " has been finalized");

        return ResponseEntity.ok().body(JSON);
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

        List<ProductDto> productDtos = new ArrayList<>();

        // Connect all products with order
        for (Long product : products) {
            orderProductService.createOrderProduct(order, product);
        }

        //register order and check if any status exist
        if(!orderStatusService.registerOrder(order))
        {
            JSON.put("success", false);
            JSON.put("message", "Cannot register order, because no status has been found");
        }

        ObjectNode payLoad = objectMapper.createObjectNode();

        MailPayloadDto mailPayloadDto = new MailPayloadDto();
        mailPayloadDto.setTo(orderDto.getEmail());
        mailPayloadDto.setSubject("Zamówienie zostało złożone - nr " + order.getId());
        mailPayloadDto.setName(orderDto.getName());
        mailPayloadDto.setSurname(orderDto.getSurname());
        mailPayloadDto.setPhone(orderDto.getPhone());
        mailPayloadDto.setOrderId(order.getId());
        mailPayloadDto.setAddress(orderDto.getAddress());
        mailPayloadDto.setProductIds(products);

//        payLoad.set("payload", objectMapper.valueToTree(mailPayloadDto));

        String url = mailServiceUrl + "/email/send-order";

        try {
            new RestTemplate().postForEntity(url, mailPayloadDto, ObjectNode.class);
        } catch (HttpStatusCodeException | ResourceAccessException e) {
            JSON.put("success", false);
            JSON.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(JSON);
        }

        JSON.put("success", true);
        JSON.put("message", "Created order: " + order.getId());

        return ResponseEntity.ok().body(JSON);
    }
}
