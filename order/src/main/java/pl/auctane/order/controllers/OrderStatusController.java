package pl.auctane.order.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import pl.auctane.order.dtos.order.ProductDto;
import pl.auctane.order.entities.OrderStatus;
import pl.auctane.order.services.OrderService;
import pl.auctane.order.services.OrderStatusService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("v1/order-status")
public class OrderStatusController {
    private final OrderStatusService orderStatusService;
    private final OrderService orderService;
    private  final ObjectMapper objectMapper;

    @Autowired
    public OrderStatusController(OrderStatusService orderStatusService, OrderService orderService, ObjectMapper objectMapper) {
        this.orderStatusService = orderStatusService;
        this.orderService = orderService;
        this.objectMapper = objectMapper;
    }

    @Value("${service.url}")
    private String serviceUrl;

    @GetMapping(value = "/get")
    public ResponseEntity<?> getAllOrderStatuses() {
        return ResponseEntity.ok().body(orderStatusService.getAllOrderStatuses());
    }

    @GetMapping(value = "/get/{orderId}")
    public ResponseEntity<?> getOrderStatus(@PathVariable("orderId") Long orderId) {
        ObjectNode JSON = objectMapper.createObjectNode();

        //check if order exist
        if(orderService.getOrderById(orderId).isEmpty()) {
            JSON.put("succes", false);
            JSON.put("message", "Order with id " + orderId + " does not exist");
            return ResponseEntity.badRequest().body(JSON);
        }

        //get order status
        OrderStatus orderStatus =  orderStatusService.getOrderStatus(orderId);

        if(orderStatus == null) {
            JSON.put("succes", false);
            JSON.put("message", "!!!Bad thing!!! Order with id " + orderId + " does not have a status");
            return ResponseEntity.badRequest().body(JSON);
        }

        return ResponseEntity.ok().body(orderStatus);
    }
}
