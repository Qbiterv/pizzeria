package pl.auctane.meal.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.auctane.meal.entities.Meal;
import pl.auctane.meal.entities.MealCategory;
import pl.auctane.meal.entities.Product;
import pl.auctane.meal.repositories.MealRepository;

import java.util.List;
import java.util.Optional;

@Service
public class MealService {
    private final MealRepository mealRepository;

    @Autowired
    public MealService(MealRepository mealRepository) {
        this.mealRepository = mealRepository;
    }

    public List<Meal> getMeals() {
        return mealRepository.findAll();
    }

    public Optional<Meal> getMeal(Long id) {
        return mealRepository.findById(id);
    }

    public void createMeal(String name, String description, MealCategory category) {
        Meal meal = new Meal();
        meal.setName(name);
        meal.setDescription(description);
        meal.setCategory(category);
        mealRepository.save(meal);
    }

    public void updateMeal(Meal meal) {
        mealRepository.save(meal);
    }

    public boolean deleteMeal(Long id) {
        if(getMeal(id).isEmpty()) return false;

        mealRepository.deleteById(id);
        return true;
    }
}
