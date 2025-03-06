package pl.auctane.meal.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.auctane.meal.entities.Ingredient;
import pl.auctane.meal.entities.Meal;
import pl.auctane.meal.entities.MealIngredient;
import pl.auctane.meal.repositories.MealIngredientRepository;

import java.util.List;
import java.util.Optional;

@Service
public class MealIngredientService {
    private final MealIngredientRepository mealIngredientRepository;

    @Autowired
    public MealIngredientService(MealIngredientRepository mealIngredientRepository) {
        this.mealIngredientRepository = mealIngredientRepository;
    }

    public List<MealIngredient> getAllMealIngredients() {
        return mealIngredientRepository.findAll();
    }

    public Optional<MealIngredient> getMealIngredient(int id) {
        return mealIngredientRepository.findById(id);
    }

    public void createMealIngredient(Meal meal, Ingredient ingredient) {
        MealIngredient mealIngredient = new MealIngredient();

        mealIngredient.setMealId(meal);
        mealIngredient.setIngredientId(ingredient);

        mealIngredientRepository.save(mealIngredient);
    }

    public void updateMealIngredient(MealIngredient mealIngredient) {
        mealIngredientRepository.save(mealIngredient);
    }

    public boolean deleteMealIngredient(int id) {
        if(getMealIngredient(id).isEmpty()) return false;

        mealIngredientRepository.deleteById(id);

        return true;
    }
}
