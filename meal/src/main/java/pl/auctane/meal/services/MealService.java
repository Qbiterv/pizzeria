package pl.auctane.meal.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.auctane.meal.entities.Meal;
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

    public Optional<Meal> getMeal(int id) {
        return mealRepository.findById(id);
    }
}
