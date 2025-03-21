package pl.auctane.meal.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.auctane.meal.dtos.meal.MealCrateDto;
import pl.auctane.meal.dtos.meal.MealEditDto;
import pl.auctane.meal.entities.Meal;
import pl.auctane.meal.entities.MealCategory;
import pl.auctane.meal.services.MealCategoryService;
import pl.auctane.meal.services.MealService;

import java.util.List;
import java.util.Optional;

// Registering API REST controller on path /v1/meal/get
@RestController
@RequestMapping("/v1/meal")
public class MealController {

    private final MealService mealService;
    private final MealCategoryService mealCategoryService;
    private final ObjectMapper objectMapper;

    @Autowired
    public MealController(MealService mealService, MealCategoryService mealCategoryService, ObjectMapper objectMapper) {
        this.mealService = mealService;
        this.mealCategoryService = mealCategoryService;
        this.objectMapper = objectMapper;
    }

    // RECEIVING ALL MEALS
    @GetMapping("/get")
    public ResponseEntity<?> getMeals() {
        List<Meal> meals = mealService.getMeals();

        if(meals.isEmpty()) return ResponseEntity.noContent().build();

        return ResponseEntity.ok().body(meals);
    }

    // GETTING SINGLE MEAL BY ID
    @GetMapping("/get/{id}")
    public ResponseEntity<?> getMeal(@PathVariable("id") Long id) {
        Optional<Meal> meal = mealService.getMeal(id);

        if(meal.isEmpty()) return ResponseEntity.noContent().build();

        return ResponseEntity.ok().body(meal.get());
    }

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addMeal(@RequestBody MealCrateDto meal) {
        ObjectNode JSON = objectMapper.createObjectNode();

        if(meal.getName() == null || meal.getName().isEmpty()) {
            JSON.put("success", false);
            JSON.put("message", "Name is mandatory");
            return ResponseEntity.badRequest().body(JSON);
        }
        if(meal.getDescription() == null || meal.getDescription().isEmpty()) {
            JSON.put("success", false);
            JSON.put("message", "Description is mandatory");
            return ResponseEntity.badRequest().body(JSON);
        }
        if(meal.getCategoryId() == null || meal.getCategoryId() <= 0) {
            JSON.put("success", false);
            JSON.put("message", "Category id is invalid");
            return ResponseEntity.badRequest().body(JSON);
        }

        //check if category exists
        Optional<MealCategory> mealCategory = mealCategoryService.findById(meal.getCategoryId());
        if(mealCategory.isEmpty()) {
            JSON.put("success", false);
            JSON.put("message", "Category with id: " + meal.getCategoryId() + " doesn't exist");
            return ResponseEntity.badRequest().body(JSON);
        }

        mealService.createMeal(meal.getName(), meal.getDescription(), mealCategory.get());

        JSON.put("success", true);
        JSON.put("message", "Created meal: " + meal.getName() + " with description: " + meal.getDescription());
        return ResponseEntity.ok().body(JSON);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteMeal(@PathVariable("id") Long id) {
        ObjectNode JSON = objectMapper.createObjectNode();

        if(mealService.deleteMeal(id)) {
            JSON.put("success", true);
            JSON.put("message", "Deleted meal with id: " + id);

            return ResponseEntity.ok().body(JSON);
        }

        JSON.put("success", false);
        JSON.put("message", "There is no meal with id: " + id);

        return ResponseEntity.badRequest().body(JSON);
    }

    @PatchMapping(value = "/edit/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> editMeal(@PathVariable("id") Long id, @RequestBody MealEditDto patch) {
        ObjectNode JSON = objectMapper.createObjectNode();

        Optional<Meal> meal = mealService.getMeal(id);

        if(meal.isEmpty()) {
            JSON.put("success", false);
            JSON.put("message", "Meal with id: " + id + " doesn't exist");

            return ResponseEntity.badRequest().body(JSON);
        }

        System.out.println(patch.getName());
        System.out.println(patch.getDescription());

        if(patch.getName() != null && !patch.getName().isEmpty()) meal.get().setName(patch.getName());
        if(patch.getDescription() != null && !patch.getDescription().isEmpty()) meal.get().setDescription(patch.getDescription());

        // verify if two values are null, then do nothing.
        if(patch.getName() == null && patch.getDescription() == null) {
            JSON.put("success", false);
            JSON.put("message", "Invalid name and description");
            return ResponseEntity.badRequest().body(JSON);
        }

        mealService.updateMeal(meal.get());

        JSON.put("success", true);
        JSON.put("message", "Updated meal with id: " + id);
        return ResponseEntity.ok().body(JSON);
    }
}
