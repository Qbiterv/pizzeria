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
import pl.auctane.order.dtos.email.MailPayloadDto;
import pl.auctane.order.dtos.email.MailStatusDto;
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

    @Value("${service.mail.url}")
    private String mailServiceUrl;

    @Value("${service.meal.url}")
    private String mealServiceUrl;

    @Autowired
    public OrderController(OrderService orderService, ObjectMapper objectMapper, OrderProductService orderProductService, OrderStatusService orderStatusService, MealModuleService mealModuleService, StatusService statusService) {
        this.orderService = orderService;
        this.objectMapper = objectMapper;
        this.orderProductService = orderProductService;
        this.orderStatusService = orderStatusService;
        this.mealModuleService = mealModuleService;
        this.statusService = statusService;
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
        MailPayloadDto mailPayloadDto = new MailPayloadDto();
        mailPayloadDto.setTo(orderDto.getEmail());
        mailPayloadDto.setSubject("Zamówienie zostało złożone - nr " + order.getId());
        mailPayloadDto.setName(orderDto.getName());
        mailPayloadDto.setSurname(orderDto.getSurname());
        mailPayloadDto.setPhone(orderDto.getPhone());
        mailPayloadDto.setOrderId(order.getId());
        mailPayloadDto.setAddress(orderDto.getAddress());
        mailPayloadDto.setProductsWithQuantity(products);

        //send email
        sendOrderEmail(order.getId());


        //order created successfully
        JSON.put("success", true);
        JSON.put("message", "Created order: " + order.getId());
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
            return ResponseEntity.badRequest().body(JSON);
        }

        //check if order status exist
        Optional<OrderStatus> orderStatus = orderStatusService.getOrderStatus(id);
        if(orderStatus.isEmpty()) {
            JSON.put("success", false);
            JSON.put("message", "Order with id " + id + " does not have status");
            return ResponseEntity.badRequest().body(JSON);
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

        //delete current orderStatus relation and create new one with status CANCELED
        orderStatusService.updateOrderStatus(orderStatus.get(), canceledStatus.get());

        //send email
        sendStatusEmail(id);

        JSON.put("success", true);
        JSON.put("message", "Order with id " + id + " has been canceled");
        return ResponseEntity.ok().body(JSON);
    }

    private void sendOrderEmail(Long id) {
        String url = mailServiceUrl + "/email/send-order/" + id;

        new RestTemplate().put(url, null);
    }
    private void sendStatusEmail(Long orderId){
        String url = mailServiceUrl + "/email/send-status/" + orderId;

        new RestTemplate().put(url, null);
    }
}
