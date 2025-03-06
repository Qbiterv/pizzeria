package pl.auctane.meal.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.auctane.meal.dtos.Ingredient.IngredientDto;
import pl.auctane.meal.entities.Ingredient;
import pl.auctane.meal.services.IngredientService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("v1/ingredient")
public class IngredientController {
    private final IngredientService ingredientService;
    private final ObjectMapper objectMapper;

    @Autowired
    public IngredientController(IngredientService ingredientService, ObjectMapper objectMapper) {
        this.ingredientService = ingredientService;
        this.objectMapper = objectMapper;
    }

    @GetMapping(value = "/get", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getIngredients() {
        List<Ingredient> ingredients = ingredientService.getAllIngredients();

        if(ingredients.isEmpty()) return ResponseEntity.noContent().build();

        return ResponseEntity.ok().body(ingredients);
    }

    @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getIngredient(@PathVariable("id") int id) {
        Optional<Ingredient> ingredient = ingredientService.getIngredient(id);

        if(ingredient.isEmpty()) return ResponseEntity.noContent().build();

        return ResponseEntity.ok().body(ingredient.get());
    }

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addIngredient(@RequestBody IngredientDto ingredientDto) {
        if(ingredientDto.getName() == null || ingredientDto.getName().isEmpty()) return ResponseEntity.noContent().build();

        ObjectNode JSON = objectMapper.createObjectNode();

        ingredientService.createIngredient(ingredientDto.getName());

        JSON.put("success", true);
        JSON.put("message", "Created ingredient: " + ingredientDto.getName());

        return ResponseEntity.ok().body(JSON);
    }

    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteMeal(@PathVariable("id") int id) {
        ObjectNode JSON = objectMapper.createObjectNode();

        if(ingredientService.deleteIngredient(id)) {
            JSON.put("success", true);
            JSON.put("message", "Deleted ingredient with id: " + id);

            return ResponseEntity.ok().body(JSON);
        }

        JSON.put("success", false);
        JSON.put("message", "Couldn't delete ingredient with id: " + id);

        return ResponseEntity.badRequest().body(JSON);
    }

    @PatchMapping(value = "/edit/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> editMeal(@PathVariable("id") int id, @RequestBody IngredientDto ingredientDto) {
        ObjectNode JSON = objectMapper.createObjectNode();

        Optional<Ingredient> ingredient = ingredientService.getIngredient(id);

        if(ingredient.isEmpty()) {
            JSON.put("success", false);
            JSON.put("message", "Ingredient with id: " + id + " doesn't exist");

            return ResponseEntity.badRequest().body(JSON);
        }

        if(ingredientDto.getName() != null && !ingredientDto.getName().isEmpty()) {
            ingredient.get().setName(ingredientDto.getName());
        } else {
            JSON.put("success", false);
            JSON.put("message", "Invalid name");
            return ResponseEntity.badRequest().body(JSON);
        }

        ingredientService.updateIngredient(ingredient.get());

        JSON.put("success", true);
        JSON.put("message", "Updated ingredient with id: " + id);

        return ResponseEntity.ok().body(JSON);
    }
}
