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
import pl.auctane.order.dtos.order.ProductWithQuantityDto;
import pl.auctane.order.entities.OrderProduct;
import pl.auctane.order.services.OrderProductService;
import pl.auctane.order.services.OrderService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/v1/order-product")
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
        List<OrderProduct> orderProducts = orderProductService.getAllOrderProducts();
        if(orderProducts.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok().body(orderProducts);
    }

    @GetMapping(value = "/get/{orderId}")
    public ResponseEntity<?> getAllProductsForOrder(@PathVariable("orderId") Long orderId) {
        ObjectNode JSON = objectMapper.createObjectNode();

        //check if order exist
        if(orderService.getOrderById(orderId).isEmpty()) {
            JSON.put("success", false);
            JSON.put("message", "Order with id " + orderId + " does not exist");
            return ResponseEntity.badRequest().body(JSON);
        }

        //get all products with quantity for order
        List<ProductWithQuantityDto> productsWithQuantity = getProductsWithQuantityForOrder(orderId);

        return ResponseEntity.ok().body(productsWithQuantity);
    }

    private List<ProductWithQuantityDto> getProductsWithQuantityForOrder(Long orderId) {
        //get list of relations
        List<OrderProduct> orderProducts = orderProductService.getAllOrderProductsForOrder(orderId);
        //initialize list of products
        List<ProductWithQuantityDto> productsWithQuantity = new ArrayList<>();

        //get product with quantity for each relation
        for (OrderProduct orderProduct : orderProducts) {
            getProductFromId(orderProduct.getProductId()).ifPresent(product -> productsWithQuantity.add(new ProductWithQuantityDto(product, orderProduct.getQuantity())));
        }

        return productsWithQuantity;
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
