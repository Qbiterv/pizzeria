package pl.auctane.meal.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.auctane.meal.entities.Ingredient;
import pl.auctane.meal.entities.Meal;
import pl.auctane.meal.entities.MealIngredient;
import pl.auctane.meal.repositories.MealIngredientRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MealIngredientService {
    private final MealIngredientRepository mealIngredientRepository;
    private final IngredientService ingredientService;

    @Autowired
    public MealIngredientService(MealIngredientRepository mealIngredientRepository, IngredientService ingredientService) {
        this.mealIngredientRepository = mealIngredientRepository;
        this.ingredientService = ingredientService;
    }

    public List<MealIngredient> getAllMealIngredients() {
        return mealIngredientRepository.findAll();
    }

    public List<Ingredient> getAllIngredientsForMeal(int mealId) {
        List<Ingredient> list = new ArrayList<>();
        mealIngredientRepository.findAllByMealId_Id(mealId).forEach(mealIngredient -> {ingredientService.getIngredient(mealIngredient.getIngredient().getId()).ifPresent(ingredient -> list.add(ingredient));});
        return list;
    }

    public Optional<MealIngredient> getMealIngredient(Long id) {
        return mealIngredientRepository.findById(id);
    }

    public void createMealIngredient(Meal meal, Ingredient ingredient) {
        MealIngredient mealIngredient = new MealIngredient();

        mealIngredient.setMeal(meal);
        mealIngredient.setIngredient(ingredient);

        mealIngredientRepository.save(mealIngredient);
    }

    public void updateMealIngredient(MealIngredient mealIngredient) {
        mealIngredientRepository.save(mealIngredient);
    }

    public boolean deleteMealIngredient(Long id) {
        if(getMealIngredient(id).isEmpty()) return false;

        mealIngredientRepository.deleteById(id);

        return true;
    }
}
