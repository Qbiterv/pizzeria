package pl.auctane.meal.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.auctane.meal.dtos.productMeal.ProductMealsListDto;
import pl.auctane.meal.entities.Meal;
import pl.auctane.meal.entities.Product;
import pl.auctane.meal.entities.ProductMeal;
import pl.auctane.meal.services.MealService;
import pl.auctane.meal.services.ProductMealService;
import pl.auctane.meal.services.ProductService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/v1/product-meal")
public class ProductMealController {
    private final ProductMealService productMealService;
    private final MealService mealService;
    private final ProductService productService;
    private final ObjectMapper objectMapper;

    @Autowired
    public ProductMealController(ProductMealService productMealService, MealService mealService, ProductService productService, ObjectMapper objectMapper) {
        this.productMealService = productMealService;
        this.mealService = mealService;
        this.productService = productService;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/get")
    public ResponseEntity<?> getProductMeals() {
        return ResponseEntity.ok().body(productMealService.getAll());
    }

    @GetMapping("/product/{id}")
    public ResponseEntity<?> getProductMeals(@PathVariable("id") int id) {
        ObjectNode JSON = objectMapper.createObjectNode();
        Optional<Product> product = productService.getProduct(id);

        if (product.isEmpty()) {
            JSON.put("success", false);
            JSON.put("message", "Product with id: " + id + " doesn't exist");

            return ResponseEntity.badRequest().body(JSON);
        }

        System.out.println(product.get());

        List<ProductMealsListDto> meals = productMealService.getProductMeals(product.get());


        System.out.println(meals);

        if(meals.isEmpty()) return ResponseEntity.noContent().build();

        return ResponseEntity.ok().body(meals);
    }

    @PostMapping(value = "/add/{id}/{mealId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addProductMeal(@PathVariable("id") int id, @PathVariable("mealId") int mealId) {
        ObjectNode JSON = objectMapper.createObjectNode();
        Optional<Product> product = productService.getProduct(id);

        if (product.isEmpty()) {
            JSON.put("success", false);
            JSON.put("message", "Product with id: " + id + " doesn't exist");

            return ResponseEntity.badRequest().body(JSON);
        }

        Optional<Meal> meal = mealService.getMeal(mealId);

        if (meal.isEmpty()) {
            JSON.put("success", false);
            JSON.put("message", "Meal with id: " + mealId + " doesn't exist");

            return ResponseEntity.badRequest().body(JSON);
        }

        ProductMeal productMeal = new ProductMeal();
        productMeal.setProduct(product.get());
        productMeal.setMeal(meal.get());

        productMealService.save(productMeal);

        JSON.put("success", true);
        JSON.put("message", "Successfully added meal: " + meal.get().getName() + " for product: " + product.get().getName());

        return ResponseEntity.ok().body(JSON);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteProductMeal(@PathVariable("id") int id) {
        ObjectNode JSON = objectMapper.createObjectNode();

        Optional<ProductMeal> productMeal = productMealService.getProductMeal(id);

        if (productMeal.isEmpty()) {
            JSON.put("success", false);
            JSON.put("message", "There is not product - meal relation with id: " + id);

            return ResponseEntity.badRequest().body(JSON);
        }

        productMealService.delete(productMeal.get());

        JSON.put("success", true);
        JSON.put("message", "Deleted product - meal relation with id: " + id);

        return ResponseEntity.ok().body(JSON);
    }
}
