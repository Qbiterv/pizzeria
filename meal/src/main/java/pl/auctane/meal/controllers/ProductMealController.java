package pl.auctane.meal.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.auctane.meal.dtos.meal.MealWithQuantityDto;
import pl.auctane.meal.dtos.productMeal.MealToSendDto;
import pl.auctane.meal.entities.Meal;
import pl.auctane.meal.entities.Product;
import pl.auctane.meal.entities.ProductMeal;
import pl.auctane.meal.services.MealService;
import pl.auctane.meal.services.ProductMealService;
import pl.auctane.meal.services.ProductService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

//passed sefel check

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
        List<ProductMeal> productMeals = productMealService.getAll();
        if (productMeals.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok().body(productMeals);
    }

    @GetMapping("/product/{id}")
    public ResponseEntity<?> getAllMealsForProduct(@PathVariable("id") Long id) {
        ObjectNode JSON = objectMapper.createObjectNode();

        Optional<Product> product = productService.getProduct(id);

        if (product.isEmpty()) {
            JSON.put("success", false);
            JSON.put("message", "Product with id: " + id + " doesn't exist");
            return ResponseEntity.badRequest().body(JSON);
        }

        List<Meal> meals = productMealService.getProductMeals(id);

        if(meals.isEmpty()) return ResponseEntity.noContent().build();

        JSON.set("meals", objectMapper.valueToTree(meals));
        return ResponseEntity.ok().body(JSON);
    }

    @GetMapping("/meals-with-quantity/{productId}")
    public ResponseEntity<?> getMealsWithQuantity(@PathVariable("productId") Long productId) {
        ObjectNode JSON = objectMapper.createObjectNode();

        Optional<Product> product = productService.getProduct(productId);

        if (product.isEmpty()) {
            JSON.put("success", false);
            JSON.put("message", "Product with id: " + productId + " doesn't exist");
            return ResponseEntity.badRequest().body(JSON);
        }

        List<Meal> meals = productMealService.getProductMeals(productId);

        //if there is only one meal, return empty list
        if (meals.size() <= 1)
            return ResponseEntity.noContent().build();

        //list of meal and its index in the list
        HashMap<Meal, Integer> mealsDictionary = new HashMap<>();
        //final list
        List<MealWithQuantityDto> mealsWithQuantity = new ArrayList<>();

        //connect meals
        for (Meal meal : meals) {
            //if meal already exist in the list, increase quantity
            if(mealsDictionary.containsKey(meal))
                mealsWithQuantity.get(mealsDictionary.get(meal)).setQuantity(mealsWithQuantity.get(mealsDictionary.get(meal)).getQuantity() + 1);
                //else put it into list and dictionary of indexes
            else {
                mealsWithQuantity.add(new MealWithQuantityDto(meal, 1));
                mealsDictionary.put(meal, meals.indexOf(meal));
            }
        }

        return ResponseEntity.ok().body(mealsWithQuantity);
    }

    @PostMapping(value = "/add/{id}/{mealId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addProductMeal(@PathVariable("id") Long id, @PathVariable("mealId") Long mealId) {
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

        ProductMeal productMeal = new ProductMeal(product.get(), meal.get());
        productMealService.create(productMeal);

        JSON.put("success", true);
        JSON.put("message", "Successfully added meal: " + meal.get().getName() + " for product: " + product.get().getName());
        return ResponseEntity.ok().body(JSON);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteProductMeal(@PathVariable("id") Long id) {
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
