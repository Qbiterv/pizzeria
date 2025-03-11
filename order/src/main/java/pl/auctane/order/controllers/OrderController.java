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

@RequestMapping
@RestController("/v1/order")
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
        List<Long> products = orderDto.getProducts();
        ObjectNode JSON = objectMapper.createObjectNode();

        Order newOrder = new Order();
        newOrder.setName(orderDto.getName());
        newOrder.setSurname(orderDto.getSurname());
        newOrder.setEmail(orderDto.getEmail());
        newOrder.setPhone(orderDto.getPhone());
        newOrder.setAddress(orderDto.getAddress());

        Order order = orderService.createOrder(newOrder);

        for (Long product : products) {
            String url = serviceUrl + "/product/get/" + product;

            ResponseEntity<ProductDto> response = null;

            try {
                response = new RestTemplate().exchange(url, HttpMethod.GET, null, ProductDto.class);
            } catch (HttpStatusCodeException | ResourceAccessException e) {
                JSON.put("success", false);
                JSON.put("message", e.getMessage());
            }

            if (response == null) {
                JSON.put("success", false);
                JSON.put("message", "Invalid product with id " + product);

                return ResponseEntity.badRequest().body(JSON);
            }

            ProductDto productDto = response.getBody();

            if (productDto == null) {
                JSON.put("success", false);
                JSON.put("message", "Invalid product with id " + product);

                return ResponseEntity.badRequest().body(JSON);
            }

            if (!response.getStatusCode().is2xxSuccessful()) {
                JSON.put("success", false);
                JSON.put("message", "Invalid product with id " + product);

                return ResponseEntity.badRequest().body(JSON);
            }

            OrderProduct orderProduct = new OrderProduct();
            orderProduct.setOrder(order);
            orderProduct.setProduct(product);

            orderProductService.createOrderProduct(order, productDto);

        }

        return ResponseEntity.ok().build();
    }
}
