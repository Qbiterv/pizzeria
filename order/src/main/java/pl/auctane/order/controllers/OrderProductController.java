package pl.auctane.order.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import pl.auctane.order.dtos.order.ProductDto;
import pl.auctane.order.dtos.orderProduct.OrderProductDto;
import pl.auctane.order.entities.Order;
import pl.auctane.order.entities.OrderProduct;
import pl.auctane.order.services.OrderProductService;
import pl.auctane.order.services.OrderService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("v1/order-product")
public class OrderProductController {
    private final OrderProductService orderProductService;
    private final OrderService orderService;
    private final ObjectMapper objectMapper;

    @Value("${service.url}")
    private String serviceUrl;

    @Autowired
    public OrderProductController(OrderProductService orderProductService, ObjectMapper objectMapper, OrderService orderService) {
        this.orderProductService = orderProductService;
        this.objectMapper = objectMapper;
        this.orderService = orderService;
    }

    @GetMapping(value = "/get")
    public ResponseEntity<?> getOrderProducts() {
        return ResponseEntity.ok().body(orderProductService.getAllOrderProducts());
    }

    @GetMapping(value = "/get/{orderId}")
    public ResponseEntity<?> getAllProductIdsForOrder(@PathVariable("orderId") Long orderId) {
        ObjectNode JSON = objectMapper.createObjectNode();

        //check if order exist
        if(orderService.getOrderById(orderId).isEmpty()) {
            JSON.put("succes", false);
            JSON.put("message", "Order with id " + orderId + " does not exist");
            return ResponseEntity.badRequest().body(JSON);
        }

        List<Long> products =  orderProductService.getAllProductIdsForOrder(orderId);

        return ResponseEntity.ok().body(products);
    }
}
