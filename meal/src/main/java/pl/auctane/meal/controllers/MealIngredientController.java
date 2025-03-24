package pl.auctane.meal.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.auctane.meal.dtos.MealIngredient.MealIngredientDto;
import pl.auctane.meal.entities.Ingredient;
import pl.auctane.meal.entities.Meal;
import pl.auctane.meal.entities.MealIngredient;
import pl.auctane.meal.services.IngredientService;
import pl.auctane.meal.services.MealIngredientService;
import pl.auctane.meal.services.MealService;
import java.util.List;
import java.util.Optional;

//passed sefel check

@RestController
@RequestMapping("v1/mealIngredient")
public class MealIngredientController {
    private final MealIngredientService mealIngredientService;
    private final IngredientService ingredientService;
    private final MealService mealService;
    private final ObjectMapper objectMapper;

    @Autowired
    public MealIngredientController(MealIngredientService mealIngredientService, ObjectMapper objectMapper, IngredientService ingredientService, MealService mealService) {
        this.mealIngredientService = mealIngredientService;
        this.objectMapper = objectMapper;
        this.ingredientService = ingredientService;
        this.mealService = mealService;
    }

    @GetMapping(value = "/get", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getMealIngredients() {
        List<MealIngredient> mealIngredients = mealIngredientService.getAllMealIngredients();
        if(mealIngredients.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok().body(mealIngredients);
    }

    @GetMapping(value = "/get/{mealId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getMealIngredient(@PathVariable("mealId") Long mealId) {
        ObjectNode JSON = objectMapper.createObjectNode();

        //check if meal exist
        if(mealService.getMeal(mealId).isEmpty()) {
            JSON.put("succes", false);
            JSON.put("message", "Meal with id " + mealId + " does not exist");
            return ResponseEntity.badRequest().body(JSON);
        }

        List<Ingredient> ingredients =  mealIngredientService.getAllIngredientsForMeal(mealId);
        if(ingredients.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok().body(ingredients);
    }

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addMealIngredient(@RequestBody MealIngredientDto mealIngredientDto) {
        ObjectNode JSON = objectMapper.createObjectNode();

        //check if meal and ingredient exist
        Optional<Meal> meal = mealService.getMeal(mealIngredientDto.getMealId());
        if(meal.isEmpty()) {
            JSON.put("success", false);
            JSON.put("message", "Meal with id: " + mealIngredientDto.getMealId() + " does not exist");
            return ResponseEntity.badRequest().body(JSON);
        }
        Optional<Ingredient> ingredient = ingredientService.getIngredient(mealIngredientDto.getIngredientId());
        if(ingredient.isEmpty()) {
            JSON.put("success", false);
            JSON.put("message", "Ingredient with id: " + mealIngredientDto.getIngredientId() + " does not exist");
            return ResponseEntity.badRequest().body(JSON);
        }

        mealIngredientService.createMealIngredient(meal.get(), ingredient.get());

        JSON.put("success", true);
        JSON.put("message", "Connected ingredient: " + ingredient.get().getName() + " with meal: " + meal.get().getName());
        return ResponseEntity.ok().body(JSON);
    }

    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteMeal(@PathVariable("id") Long id) {
        ObjectNode JSON = objectMapper.createObjectNode();

        Optional<MealIngredient> mealIngredient = mealIngredientService.getMealIngredient(id);

        if(mealIngredient.isEmpty()) {
            JSON.put("success", false);
            JSON.put("message", "Meal-ingredient with id: " + id + " does not exist");
            return ResponseEntity.badRequest().body(JSON);
        }

        mealIngredientService.deleteMealIngredient(id);

        JSON.put("success", true);
        JSON.put("message", "Deleted meal ingredient with id: " + id);
        return ResponseEntity.ok().body(JSON);
    }

    @PatchMapping(value = "/edit/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> editMeal(@PathVariable("id") Long id, @RequestBody MealIngredientDto mealIngredientDto) {
        ObjectNode JSON = objectMapper.createObjectNode();

        Optional<MealIngredient> mealIngredient = mealIngredientService.getMealIngredient(id);

        //check if relation exist
        if(mealIngredient.isEmpty()) {
            JSON.put("success", false);
            JSON.put("message", "Meal-ingredient with id: " + id + " does not exist");
            return ResponseEntity.badRequest().body(JSON);
        }

        //change values
        if(mealIngredientDto.getMealId() != null && mealIngredientDto.getMealId() != 0) {
            //check if new meal exist
            Optional<Meal> meal = mealService.getMeal(mealIngredientDto.getMealId());
            if(meal.isEmpty()) {
                JSON.put("success", false);
                JSON.put("message", "Meal with id: " + mealIngredientDto.getMealId() + " does not exist");
                return ResponseEntity.badRequest().body(JSON);
            }
            mealIngredient.get().setMeal(meal.get());
        }
        if(mealIngredientDto.getIngredientId() != null && mealIngredientDto.getIngredientId() != 0) {
            //check if new ingredient exist
            Optional<Ingredient> ingredient = ingredientService.getIngredient(mealIngredientDto.getIngredientId());
            if(ingredient.isEmpty()){
                JSON.put("success", false);
                JSON.put("message", "Ingredient with id: " + mealIngredientDto.getIngredientId() + " does not exist");
                return ResponseEntity.badRequest().body(JSON);
            }
            mealIngredient.get().setIngredient(ingredient.get());
        }

        mealIngredientService.updateMealIngredient(mealIngredient.get());

        JSON.put("success", true);
        JSON.put("message", "Updated meal with id: " + id);
        return ResponseEntity.ok().body(JSON);
    }
}