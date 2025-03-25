package pl.auctane.order.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import pl.auctane.order.dtos.OrderProductsDto;
import pl.auctane.order.dtos.order.ProductWithQuantityAndMealsDto;
import pl.auctane.order.dtos.order.ProductWithQuantityDto;
import pl.auctane.order.entities.OrderProduct;
import pl.auctane.order.services.MealModuleService;
import pl.auctane.order.services.OrderProductService;
import pl.auctane.order.services.OrderService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/v1/order-product")
public class OrderProductController {
    private final OrderProductService orderProductService;
    private final MealModuleService mealModuleService;
    private final OrderService orderService;
    private final ObjectMapper objectMapper;

    @Autowired
    public OrderProductController(OrderProductService orderProductService, MealModuleService mealModuleService, ObjectMapper objectMapper, OrderService orderService) {
        this.orderProductService = orderProductService;
        this.mealModuleService = mealModuleService;
        this.objectMapper = objectMapper;
        this.orderService = orderService;
    }

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
        List<ProductWithQuantityDto> productsWithQuantity = getProductsWithQuantity(orderId);

        return ResponseEntity.ok().body(productsWithQuantity);
    }

    @GetMapping(value = "get-order-products/{orderId}")
    public ResponseEntity<?> getOrderProducts(@PathVariable("orderId") Long orderId) {
        ObjectNode JSON = objectMapper.createObjectNode();

        //check if order exist
        if(orderService.getOrderById(orderId).isEmpty()) {
            JSON.put("success", false);
            JSON.put("message", "Order with id " + orderId + " does not exist");
            return ResponseEntity.badRequest().body(JSON);
        }

        List<ProductWithQuantityAndMealsDto> productsWithQuantity = mealModuleService.getProductWithMealsList(getProductsWithQuantity(orderId));

        return ResponseEntity.ok().body(new OrderProductsDto(productsWithQuantity));
    }

    private List<ProductWithQuantityDto> getProductsWithQuantity(Long orderId) {
        //get list of relations
        List<OrderProduct> orderProducts = orderProductService.getAllOrderProductsForOrder(orderId);
        //initialize list of products
        List<ProductWithQuantityDto> productsWithQuantity = new ArrayList<>();

        //get product with quantity for each relation
        for (OrderProduct orderProduct : orderProducts) {
            mealModuleService.getProductFromId(orderProduct.getProductId()).ifPresent(product -> productsWithQuantity.add(new ProductWithQuantityDto(product, orderProduct.getQuantity())));
        }

        return productsWithQuantity;
    }
}
