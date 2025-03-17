package pl.auctane.order.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import pl.auctane.order.dtos.status.MailStatusPayloadDto;
import pl.auctane.order.entities.Order;
import pl.auctane.order.entities.OrderStatus;
import pl.auctane.order.entities.Status;
import pl.auctane.order.services.OrderService;
import pl.auctane.order.services.OrderStatusService;
import pl.auctane.order.services.StatusService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("v1/order-status")
public class OrderStatusController {
    private final OrderStatusService orderStatusService;
    private final OrderService orderService;
    private final StatusService statusService;
    private  final ObjectMapper objectMapper;

    @Autowired
    public OrderStatusController(OrderStatusService orderStatusService, OrderService orderService, StatusService statusService, ObjectMapper objectMapper) {
        this.orderStatusService = orderStatusService;
        this.orderService = orderService;
        this.statusService = statusService;
        this.objectMapper = objectMapper;
    }

    @Value("${service.url}")
    private String serviceUrl;

    @Value("${service.mail.url}")
    private String mailServiceUrl;

    @GetMapping(value = "/get")
    public ResponseEntity<?> getAllOrderStatuses() {
        return ResponseEntity.ok().body(orderStatusService.getAllOrderStatuses());
    }

    @GetMapping(value = "/get/{orderId}")
    public ResponseEntity<?> getOrderStatus(@PathVariable("orderId") Long orderId) {
        ObjectNode JSON = objectMapper.createObjectNode();

        //check if order exist
        if(orderService.getOrderById(orderId).isEmpty()) {
            JSON.put("successa", false);
            JSON.put("message", "Order with id " + orderId + " does not exist");
            return ResponseEntity.badRequest().body(JSON);
        }

        //get order status
        Optional<OrderStatus> orderStatus =  orderStatusService.getOrderStatus(orderId);

        if(orderStatus.isEmpty()) {
            JSON.put("success", false);
            JSON.put("message", "!!!Bad thing happened!!! Order with id " + orderId + " does not have a status");
            return ResponseEntity.badRequest().body(JSON);
        }

        return ResponseEntity.ok().body(orderStatus.get().getStatus());
    }

    @PutMapping(value = "/move-to-next-state/{orderId}")
    public ResponseEntity<?> moveToNextState(@PathVariable("orderId") Long orderId) {
        ObjectNode JSON = objectMapper.createObjectNode();

        Optional<Order> order = orderService.getOrderById(orderId);

        //check if order exist
        if(order.isEmpty()) {
            JSON.put("success", false);
            JSON.put("message", "Order with id " + orderId + " does not exist");
            return ResponseEntity.badRequest().body(JSON);
        }

        Optional<OrderStatus> orderStatus = orderStatusService.getOrderStatus(orderId);

        if(orderStatus.isEmpty()) {
            JSON.put("success", false);
            JSON.put("message", "!!!Bad thing happened!!! Order with id " + orderId + " does not have a status");
            return ResponseEntity.badRequest().body(JSON);
        }

        List<Status> allStatuses = statusService.getAllStatuses();
        Status status = orderStatus.get().getStatus();
        int indexOfStatus  = allStatuses.indexOf(status);

        if(indexOfStatus == allStatuses.size() - 1) {
            JSON.put("success", false);
            JSON.put("message", "Order with id " + orderId + " is on the last status");
            return ResponseEntity.badRequest().body(JSON);
        }

        if(indexOfStatus == allStatuses.size() - 2) {
            orderService.setFinalized(order.get());
        }

        Status nextStatus = allStatuses.get(indexOfStatus + 1);

        MailStatusPayloadDto mailStatusPayloadDto = new MailStatusPayloadDto();
        mailStatusPayloadDto.setOrderId(orderId);
        mailStatusPayloadDto.setTo(order.get().getEmail());
        mailStatusPayloadDto.setSurname(order.get().getSurname());
        mailStatusPayloadDto.setName(order.get().getName());
        mailStatusPayloadDto.setSubject("Aktualizacja statusu zamówienia " + orderId);
        mailStatusPayloadDto.setStatus(nextStatus.getName());

        try {
            new RestTemplate().postForEntity(mailServiceUrl + "/email/send-status", mailStatusPayloadDto, ObjectNode.class);
        } catch (Exception e) {
            JSON.put("success", false);
            JSON.put("message", "!!!Bad thing happened!!! Could not send email with status update");
            return ResponseEntity.badRequest().body(JSON);
        }

        orderStatusService.updateOrderStatus(orderStatus.get(), nextStatus);

        JSON.put("success", true);
        JSON.put("message", "Order with id " + orderId + " successfully moved to next state");
        return ResponseEntity.ok().body(JSON);
    }

    @PutMapping(value = "/move-to-previous-state/{orderId}")
    public ResponseEntity<?> moveToPreviousState(@PathVariable("orderId") Long orderId) {
        ObjectNode JSON = objectMapper.createObjectNode();

        Optional<Order> order = orderService.getOrderById(orderId);

        //check if order exist
        if(order.isEmpty()) {
            JSON.put("success", false);
            JSON.put("message", "Order with id " + orderId + " does not exist");
            return ResponseEntity.badRequest().body(JSON);
        }

        Optional<OrderStatus> orderStatus = orderStatusService.getOrderStatus(orderId);

        if(orderStatus.isEmpty()) {
            JSON.put("success", false);
            JSON.put("message", "!!!Bad thing happened!!! Order with id " + orderId + " does not have a status");
            return ResponseEntity.badRequest().body(JSON);
        }

        List<Status> allStatuses = statusService.getAllStatuses();
        Status status = orderStatus.get().getStatus();
        int indexOfStatus  = allStatuses.indexOf(status);

        if(indexOfStatus == 0) {
            JSON.put("success", false);
            JSON.put("message", "Order with id " + orderId + " is on the first status");
            return ResponseEntity.badRequest().body(JSON);
        }

        orderStatusService.updateOrderStatus(orderStatus.get(), allStatuses.get(indexOfStatus - 1));

        JSON.put("success", true);
        JSON.put("message", "Order with id " + orderId + " successfully moved to previous state");
        return ResponseEntity.ok().body(JSON);
    }

    @PutMapping(value = "/set-status/{orderId}/{statusId}")
    public ResponseEntity<?> setOrderStatus(@PathVariable("orderId") Long orderId, @PathVariable("statusId") Long statusId) {
        ObjectNode JSON = objectMapper.createObjectNode();

        Optional<Order> order = orderService.getOrderById(orderId);

        //check if order exist
        if(order.isEmpty()) {
            JSON.put("success", false);
            JSON.put("message", "Order with id " + orderId + " does not exist");
            return ResponseEntity.badRequest().body(JSON);
        }

        Optional<OrderStatus> orderStatus = orderStatusService.getOrderStatus(orderId);

        if(orderStatus.isEmpty()) {
            JSON.put("success", false);
            JSON.put("message", "!!!Bad thing happened!!! Order with id " + orderId + " does not have a status");
            return ResponseEntity.badRequest().body(JSON);
        }

        Optional<Status> newStatus = statusService.getStatusById(statusId);

        if(newStatus.isEmpty()) {
            JSON.put("success", false);
            JSON.put("message", "Status with id " + statusId + " doest not exist");
            return ResponseEntity.badRequest().body(JSON);
        }

        if(newStatus.get().equals(orderStatus.get().getStatus())) {
            JSON.put("success", false);
            JSON.put("message", "Order with id " + orderId + " is already on status with id " + statusId);
        }

        orderStatusService.updateOrderStatus(orderStatus.get(), newStatus.get());

        JSON.put("success", true);
        JSON.put("message", "Set status id to " + statusId + " for order with id " + orderId);
        return ResponseEntity.ok().body(JSON);
    }
}
