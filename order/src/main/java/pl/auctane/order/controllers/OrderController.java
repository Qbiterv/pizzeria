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
import pl.auctane.order.dtos.order.*;
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

        List<ProductWithQuantityDto> products;

        try {
            products = getValidProductList(orderDto.getProducts());
        } catch (IllegalArgumentException e) {
            //product with given id does not exist
            JSON.put("success", false);
            JSON.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(JSON);
        }

        // Create order
        Order order = orderService.createOrder(orderDto);

        //register order and check if any status exist
        try{
            orderStatusService.registerOrder(order);
        } catch (IllegalStateException e) {
            //no status found in the database

            //delete order
            orderService.deleteOrder(order);

            JSON.put("success", false);
            JSON.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(JSON);
        }

        // Connect all products with order
        for (ProductWithQuantityDto productWithQuantity : products)
            orderProductService.createOrderProduct(order, productWithQuantity);


        //send email
        try {
            MailPayloadDto mailPayloadDto = new MailPayloadDto();
            mailPayloadDto.setTo(orderDto.getEmail());
            mailPayloadDto.setSubject("Zamówienie zostało złożone - nr " + order.getId());
            mailPayloadDto.setName(orderDto.getName());
            mailPayloadDto.setSurname(orderDto.getSurname());
            mailPayloadDto.setPhone(orderDto.getPhone());
            mailPayloadDto.setOrderId(order.getId());
            mailPayloadDto.setAddress(orderDto.getAddress());
            mailPayloadDto.setProductsWithQuantity(products);

            sendOrderEmail(mailPayloadDto);
        } catch (Exception e) {
            JSON.put("success", false);
            JSON.put("message", "Failed to send email, error: " + e.getMessage());
            return ResponseEntity.badRequest().body(JSON);
        }

        //order created successfully
        JSON.put("success", true);
        JSON.put("message", order.getId());
        return ResponseEntity.ok().body(JSON);
    }

    private List<ProductWithQuantityDto> getValidProductList(List<ProductIdWithQuantityDto> productIds) throws IllegalArgumentException{
        List<ProductWithQuantityDto> productsWithQuantity = new ArrayList<>();

        // Check if all products exist and add them to list
        for (ProductIdWithQuantityDto productIdWithQuantity : productIds) {

            if (productIdWithQuantity.getProductId() == null || productIdWithQuantity.getProductId() < 1 || productIdWithQuantity.getQuantity() < 1)
                throw new IllegalArgumentException("Product id or quantity is invalid");

            Optional<ProductDto> product = getProductFromId(productIdWithQuantity.getProductId());

            if (product.isEmpty()) {
                throw new IllegalArgumentException("Product with id " + productIdWithQuantity.getProductId() + " does not exist");
            }

            //add product to list
            productsWithQuantity.add(productIdWithQuantity.toProductWithQuantityDto(product.get()));
        }

        return productsWithQuantity;
    }

    private void sendOrderEmail(MailPayloadDto mailPayloadDto) throws Exception {
        String url = mailServiceUrl + "/email/send-order";

        new RestTemplate().postForEntity(url, mailPayloadDto, ObjectNode.class);
    }
    private Optional<ProductDto> getProductFromId(Long product) {
        String url = serviceUrl + "/product/get/" + product;

        ResponseEntity<ProductDto> response = null;

        try {
            response = new RestTemplate().getForEntity(url, ProductDto.class);
        } catch (HttpStatusCodeException | ResourceAccessException e) {
            return Optional.empty();
        }

        return Optional.ofNullable(response.getBody());
    }
}
