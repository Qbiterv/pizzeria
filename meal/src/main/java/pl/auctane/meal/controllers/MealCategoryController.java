package pl.auctane.meal.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.auctane.meal.entities.MealCategory;
import pl.auctane.meal.services.MealCategoryService;

import java.util.List;
import java.util.Optional;

//passed sefel check

@RestController
@RequestMapping("v1/meal-category")
public class MealCategoryController {
    private final MealCategoryService mealCategoryService;
    private final ObjectMapper objectMapper;

    @Autowired
    public MealCategoryController(MealCategoryService mealCategoryService, ObjectMapper objectMapper) {
        this.mealCategoryService = mealCategoryService;
        this.objectMapper = objectMapper;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getMealCategory() {
        List<MealCategory> mealCategories = mealCategoryService.findAll();
        if(mealCategories.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok().body(mealCategories);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getMealCategoryById(@PathVariable Long id) {
        Optional<MealCategory> mealCategory = mealCategoryService.findById(id);
        if(mealCategory.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok().body(mealCategory);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addMealCategory(@RequestBody MealCategory mealCategory) {
        ObjectNode JSON = objectMapper.createObjectNode();

        if(mealCategory.getName() == null || mealCategory.getName().isEmpty()) {
            JSON.put("success", false);
            JSON.put("message", "Name is mandatory");
            return ResponseEntity.badRequest().body(JSON);
        }

        mealCategoryService.save(mealCategory);

        JSON.put("success", true);
        JSON.put("message", "Created meal category: " + mealCategory.getName());
        return ResponseEntity.ok().body(JSON);
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteMealCategory(@PathVariable("id") Long id) {
        ObjectNode JSON = objectMapper.createObjectNode();

        Optional<MealCategory> mealCategory = mealCategoryService.findById(id);

        if(mealCategory.isEmpty()) {
            JSON.put("success", false);
            JSON.put("message", "Meal category with id " + id + " does not exist");
            return ResponseEntity.badRequest().body(JSON);
        }

        mealCategoryService.deleteById(id);

        JSON.put("success", true);
        JSON.put("message", "Deleted meal category: " + mealCategory.get().getName());
        return ResponseEntity.ok().body(JSON);
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateMealCategory(@PathVariable("id") Long id, @RequestBody MealCategory mealCategory) {
        ObjectNode JSON = objectMapper.createObjectNode();

        Optional<MealCategory> mealCategoryToUpdate = mealCategoryService.findById(id);

        if(mealCategoryToUpdate.isEmpty()) {
            JSON.put("success", false);
            JSON.put("message", "Meal category with id " + mealCategory.getId() + " does not exist");
            return ResponseEntity.badRequest().body(JSON);
        }

        if(mealCategory.getName() == null || mealCategory.getName().isEmpty()) {
            JSON.put("success", false);
            JSON.put("message", "Name is mandatory");
            return ResponseEntity.badRequest().body(JSON);
        }

        mealCategoryToUpdate.get().setName(mealCategory.getName());
        mealCategoryService.save(mealCategoryToUpdate.get());

        JSON.put("success", true);
        JSON.put("message", "Updated meal category: " + mealCategory.getName());
        return ResponseEntity.ok().body(JSON);
    }
}
