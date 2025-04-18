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
import org.springframework.web.client.RestTemplate;
import pl.auctane.order.dtos.email.MailDto;
import pl.auctane.order.dtos.order.*;
import pl.auctane.order.dtos.product.ProductWithQuantityAndMealsDto;
import pl.auctane.order.entities.Order;
import pl.auctane.order.entities.OrderStatus;
import pl.auctane.order.entities.Status;
import pl.auctane.order.enums.StatusType;
import pl.auctane.order.services.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/order")
public class OrderController {
    private final OrderService orderService;
    private final ObjectMapper objectMapper;
    private final OrderProductService orderProductService;
    private final OrderStatusService orderStatusService;
    private final MealModuleService mealModuleService;
    private final StatusService statusService;
    private final RabbitSenderService rabbitSenderService;

    @Value("${service.mail.url}")
    private String mailServiceUrl;

    @Value("${service.meal.url}")
    private String mealServiceUrl;

    @Autowired
    public OrderController(OrderService orderService, ObjectMapper objectMapper, OrderProductService orderProductService, OrderStatusService orderStatusService, MealModuleService mealModuleService, StatusService statusService, RabbitSenderService rabbitSenderService) {
        this.orderService = orderService;
        this.objectMapper = objectMapper;
        this.orderProductService = orderProductService;
        this.orderStatusService = orderStatusService;
        this.mealModuleService = mealModuleService;
        this.statusService = statusService;
        this.rabbitSenderService = rabbitSenderService;
    }

    @GetMapping(value = "/get")
    public ResponseEntity<?> getOrders() {
        List<Order> orders = orderService.getOrders();
        if (orders.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok().body(orders);
    }

    @GetMapping(value = "/get/{id}")
    public ResponseEntity<?> getOrder(@PathVariable Long id) {
        Optional<Order> order = orderService.getOrderById(id);
        if(order.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok().body(order.get());
    }

    @GetMapping("/get/finalized={finalized}")
    public ResponseEntity<?> getOrdersByStatus(@PathVariable("finalized") boolean finalized) {
        List<Order> orders = orderService.getOrdersByStatus(finalized);
        if (orders.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok().body(orders);
    }

    @GetMapping("/get/email={email}")
    public ResponseEntity<?> getOrdersByEmail(@PathVariable("email") String email) {
        List<Order> orders = orderService.getOrdersByEmail(email);
        if (orders.isEmpty()) return ResponseEntity.noContent().build();
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

    @PostMapping(value = "/create", consumes =  {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> createOrder(@Valid @RequestBody OrderDto orderDto, BindingResult bindingResult) {
        ObjectNode JSON = objectMapper.createObjectNode();

        // Bean validation
        if (bindingResult.hasErrors()) {
            JSON.put("success", false);
            JSON.put("message", bindingResult.getAllErrors().stream().map(e -> ((FieldError) e).getField() + " " + e.getDefaultMessage()).collect(Collectors.joining(", ")));
            return ResponseEntity.badRequest().body(JSON);
        }

        List<ProductWithQuantityAndMealsDto> products;

        try {
            products = mealModuleService.getValidProductList(orderDto.getProducts());
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
        for (ProductWithQuantityAndMealsDto productWithQuantity : products)
            orderProductService.createOrderProduct(order, productWithQuantity);

        //send email
        sendOrderEmail(order.getId());

        //order created successfully
        JSON.put("success", true);
        JSON.put("message", order.getId());
        return ResponseEntity.ok().body(JSON);
    }

    @DeleteMapping(value = "/cancel-order/{id}")
    public ResponseEntity<?> cancelOrder(@PathVariable("id") Long id) {
        ObjectNode JSON = objectMapper.createObjectNode();

        //check if order exist
        Optional<Order> order = orderService.getOrderById(id);
        if(order.isEmpty()) {
            JSON.put("success", false);
            JSON.put("message", "Order with id " + id + " does not exist");
            return ResponseEntity.ok().body(JSON);
        }

        //check if order status exist
        Optional<OrderStatus> orderStatus = orderStatusService.getOrderStatus(id);
        if(orderStatus.isEmpty()) {
            JSON.put("success", false);
            JSON.put("message", "Order with id " + id + " does not have status");
            return ResponseEntity.ok().body(JSON);
        }

        //check if order is on status CREATED
        try {
            if (orderStatus.get().getStatus().getType() != StatusType.CREATED) {
                JSON.put("success", false);
                JSON.put("message", "Order with id " + id + " is not on status CREATED");
                return ResponseEntity.badRequest().body(JSON);
            }
        } catch (IllegalStateException e) {
            JSON.put("success", false);
            JSON.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(JSON);
        }

        //get status with type CANCELED
        Optional<Status> canceledStatus = statusService.getCanceledStatus();

        //check if status CANCELED exist
        if(canceledStatus.isEmpty()) {
            JSON.put("success", false);
            JSON.put("message", "There is no status with type CANCELED in the database");
            return ResponseEntity.badRequest().body(JSON);
        }

        //set order status to CANCELED and set order as finalized
        orderStatusService.updateOrderStatus(orderStatus.get(), canceledStatus.get());
        orderService.setFinalized(order.get());

        //send email
        sendStatusEmail(id);

        JSON.put("success", true);
        JSON.put("message", "Order with id " + id + " has been canceled");
        return ResponseEntity.ok().body(JSON);
    }

    private void sendOrderEmail(Long orderId) {

        //rabitMQ sender
        rabbitSenderService.sendEmail(new MailDto(orderId, "order"));

    }
    private void sendStatusEmail(Long orderId){

        rabbitSenderService.sendEmail(new MailDto(orderId, "status"));

    }
}
