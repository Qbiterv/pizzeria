package pl.auctane.meal.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.auctane.meal.entities.Meal;
import pl.auctane.meal.services.MealService;

import java.util.List;
import java.util.Optional;

// Registering API REST controller on path /v1/meal/get
@RestController
@RequestMapping("/v1/meal")
public class MealController {

    private final MealService mealService;

    @Autowired
    public MealController(MealService mealService) {
        this.mealService = mealService;
    }

    // RECEIVING ALL MEALS
    @GetMapping("/get")
    public ResponseEntity<Object> getMeals() {
        List<Meal> meals = mealService.getMeals();

//        if(meals.isEmpty()) {
//            return ResponseEntity.noContent().build();
//        }

        if(meals.isEmpty()) return ResponseEntity.ok().body("test");

        return ResponseEntity.ok(meals);
    }

    // GETTING SINGLE MEAL BY ID
    @GetMapping("/get/{id}")
    public ResponseEntity<Object> getMeal(@PathVariable("id") int id) {
        Optional<Meal> meal = mealService.getMeal(id);

        if(meal.isEmpty()) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(meal.get());
    }

    // TODO: Adding meal via API, editing meal via api, deleting meal via api.
}
