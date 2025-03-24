package pl.auctane.meal.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import pl.auctane.meal.dtos.meal.MealCrateDto;
import pl.auctane.meal.dtos.meal.MealEditDto;
import pl.auctane.meal.entities.Meal;
import pl.auctane.meal.entities.MealCategory;
import pl.auctane.meal.services.MealCategoryService;
import pl.auctane.meal.services.MealService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

//passed sefel check

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

    @GetMapping("/get")
    public ResponseEntity<?> getMeals() {
        List<Meal> meals = mealService.getMeals();
        if(meals.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok().body(meals);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getMeal(@PathVariable("id") Long id) {
        Optional<Meal> meal = mealService.getMeal(id);
        if(meal.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok().body(meal.get());
    }

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addMeal(@Valid @RequestBody MealCrateDto meal, BindingResult bindingResult) {
        ObjectNode JSON = objectMapper.createObjectNode();

        // Bean validation
        if (bindingResult.hasErrors()) {
            JSON.put("success", false);
            JSON.put("message", bindingResult.getAllErrors().stream().map(e -> ((FieldError) e).getField() + " " + e.getDefaultMessage()).collect(Collectors.joining(", ")));
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

        Optional<Meal> meal = mealService.getMeal(id);

        if(meal.isEmpty()) {
            JSON.put("success", false);
            JSON.put("message", "Meal with id: " + id + " does not exist");
            return ResponseEntity.badRequest().body(JSON);
        }

        mealService.deleteMeal(id);

        JSON.put("success", true);
        JSON.put("message", "Deleted meal with id: " + id);
        return ResponseEntity.badRequest().body(JSON);
    }

    @PatchMapping(value = "/edit/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> editMeal(@PathVariable("id") Long id, @RequestBody MealEditDto patch) {
        ObjectNode JSON = objectMapper.createObjectNode();

        Optional<Meal> meal = mealService.getMeal(id);

        if(meal.isEmpty()) {
            JSON.put("success", false);
            JSON.put("message", "Meal with id: " + id + " does not exist");
            return ResponseEntity.badRequest().body(JSON);
        }

        if(patch.getName() != null && !patch.getName().isEmpty()) meal.get().setName(patch.getName());
        if(patch.getDescription() != null && !patch.getDescription().isEmpty()) meal.get().setDescription(patch.getDescription());
        if(patch.getCategoryId() != null) {
            Optional<MealCategory> mealCategory = mealCategoryService.findById(patch.getCategoryId());
            if (mealCategory.isEmpty()) {
                JSON.put("success", false);
                JSON.put("message", "Category with id: " + patch.getCategoryId() + " does not exist");
                return ResponseEntity.badRequest().body(JSON);
            }
            meal.get().setCategory(mealCategory.get());
        }

        mealService.updateMeal(meal.get());

        JSON.put("success", true);
        JSON.put("message", "Updated meal with id: " + id);
        return ResponseEntity.ok().body(JSON);
    }
}
