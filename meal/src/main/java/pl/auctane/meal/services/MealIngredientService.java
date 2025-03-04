package pl.auctane.meal.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.auctane.meal.repositories.MealIngredientRepository;

@Service
public class MealIngredientService {
    private final MealIngredientRepository mealIngredientRepository;

    @Autowired
    public MealIngredientService(MealIngredientRepository mealIngredientRepository) {
        this.mealIngredientRepository = mealIngredientRepository;
    }
}
