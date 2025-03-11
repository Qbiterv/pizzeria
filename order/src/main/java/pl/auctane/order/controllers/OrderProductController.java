package pl.auctane.order.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import pl.auctane.order.dtos.order.ProductDto;
import pl.auctane.order.services.OrderProductService;
import pl.auctane.order.services.OrderService;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("v1/order-product")
public class OrderProductController {
    private final OrderProductService orderProductService;
    private final OrderService orderService;
    private final ObjectMapper objectMapper;

    @Autowired
    public OrderProductController(OrderProductService orderProductService, ObjectMapper objectMapper, OrderService orderService) {
        this.orderProductService = orderProductService;
        this.objectMapper = objectMapper;
        this.orderService = orderService;
    }

    @Value("${service.url}")
    private String serviceUrl;

    @GetMapping(value = "/get")
    public ResponseEntity<?> getOrderProducts() {
        return ResponseEntity.ok().body(orderProductService.getAllOrderProducts());
    }

    @GetMapping(value = "/get/{orderId}")
    public ResponseEntity<?> getAllProductsForOrder(@PathVariable("orderId") Long orderId) {
        ObjectNode JSON = objectMapper.createObjectNode();

        //check if order exist
        if(orderService.getOrderById(orderId).isEmpty()) {
            JSON.put("succes", false);
            JSON.put("message", "Order with id " + orderId + " does not exist");
            return ResponseEntity.badRequest().body(JSON);
        }

        //get list of product ids
        List<Long> productIds =  orderProductService.getAllProductIdsForOrder(orderId);

        List<ProductDto> products = new ArrayList<>();

        //get product for each id
        for (Long productId : productIds) {
            ResponseEntity<ProductDto> response = null;
            String url = serviceUrl + "/product/get/" + productId;
            try {
                response = new RestTemplate().getForEntity(url, ProductDto.class);
                System.out.println(response.getBody());
            } catch (HttpStatusCodeException | ResourceAccessException e) {
                JSON.put("success", false);
                JSON.put("message", e.getMessage());
                return  ResponseEntity.badRequest().body(JSON);
            }

            ProductDto productDto = response.getBody();

            if (productDto == null) {
                JSON.put("success", false);
                JSON.put("message", "Request failed");
                return ResponseEntity.badRequest().body(JSON);
            }

            if (response.getStatusCode().isSameCodeAs(HttpStatus.NO_CONTENT)) {
                JSON.put("success", false);
                JSON.put("message", "Product with id " + productId + " does not exist");
                return ResponseEntity.badRequest().body(JSON);
            }
            products.add(productDto);
        }

        return ResponseEntity.ok().body(products);
    }
}
