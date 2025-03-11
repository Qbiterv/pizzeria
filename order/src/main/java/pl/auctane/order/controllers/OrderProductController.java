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
@RequestMapping("/v1/order-product")
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
        List<OrderProduct> orderProducts = orderProductService.getAllOrderProducts();

        if(orderProducts.isEmpty()) return ResponseEntity.noContent().build();

        return ResponseEntity.ok().body(orderProducts);
    }

    @GetMapping(value = "/get/{orderId}", produces = MediaType.APPLICATION_JSON_VALUE)
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

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addOrderProduct(@RequestBody OrderProductDto orderProductDto) {

        ObjectNode JSON = objectMapper.createObjectNode();

        //check if order and product exist
        Optional<Order> order = orderService.getOrderById(orderProductDto.getOrderId());
        if(order.isEmpty()) {
            JSON.put("success", false);
            JSON.put("message", "Order with id: " + orderProductDto.getOrderId() + " does not exist");
            return ResponseEntity.badRequest().body(JSON);
        }

        //http request for product with id
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ProductDto> request = new HttpEntity<>(headers);

        String url = serviceUrl + "/product/get" + orderProductDto.getProductId();

        ResponseEntity<ProductDto> response = null;

        try {
            response = new RestTemplate().exchange(url, HttpMethod.GET, null, ProductDto.class);
        } catch (HttpStatusCodeException | ResourceAccessException e) {
            JSON.put("success", false);
            JSON.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(JSON);
        }
        //?
        if (response == null){
           JSON.put("success", false);
           JSON.put("message", "No response from server");
           return ResponseEntity.badRequest().body(JSON);
        }
        if (response.getStatusCode().isSameCodeAs(HttpStatus.NO_CONTENT)) {
            JSON.put("success", false);
            JSON.put("message", "Product with id " + orderProductDto.getProductId() + " does not exist");
            return ResponseEntity.badRequest().body(JSON);
        }

        orderProductService.createOrderProduct(order.get(), orderProductDto.getProductId());

        JSON.put("success", true);
        JSON.put("message", "Connected order: " + orderProductDto.getProductId() + " with product: " + orderProductDto.getProductId());

        return ResponseEntity.ok().body(JSON);
    }

    /*
    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteMeal(@PathVariable("id") Long id) {
        ObjectNode JSON = objectMapper.createObjectNode();

        if(mealIngredientService.deleteMealIngredient(id)) {
            JSON.put("success", true);
            JSON.put("message", "Deleted meal ingredient with id: " + id);

            return ResponseEntity.ok().body(JSON);
        }

        JSON.put("success", false);
        JSON.put("message", "Couldn't delete ingredient meal with id: " + id);

        return ResponseEntity.badRequest().body(JSON);
    }

    @PatchMapping(value = "/edit/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> editMeal(@PathVariable("id") Long id, @RequestBody MealIngredientDto mealIngredientDto) {
        ObjectNode JSON = objectMapper.createObjectNode();

        //check if mealIngredient exist
        Optional<MealIngredient> mealIngredient = mealIngredientService.getMealIngredient(id);
        if(mealIngredient.isEmpty()) {
            JSON.put("success", false);
            JSON.put("message", "Meal ingredient with id: " + id + " does not exist");
            return ResponseEntity.badRequest().body(JSON);
        }

        //check if there is something to patch
        if(mealIngredientDto.getIngredientId() == 0 && mealIngredientDto.getMealId() == 0)
            return ResponseEntity.noContent().build();

        //there is something to change inside
        if(mealIngredientDto.getMealId() != 0) {
            //check if meal exist and update
            Optional<Meal> meal = mealService.getMeal(mealIngredientDto.getMealId());
            if(!meal.isEmpty())
                mealIngredient.get().setMeal(meal.get());
        }
        if(mealIngredientDto.getIngredientId() != 0) {
            //check if ingredient exist and update
            Optional<Ingredient> ingredient = ingredientService.getIngredient(mealIngredientDto.getIngredientId());
            if(!ingredient.isEmpty())
                mealIngredient.get().setIngredient(ingredient.get());
        }

        mealIngredientService.updateMealIngredient(mealIngredient.get());

        JSON.put("success", true);
        JSON.put("message", "Updated meal with id: " + id);

        return ResponseEntity.ok().body(JSON);
    }
    */
}
