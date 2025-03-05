package pl.auctane.meal.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.auctane.meal.dtos.MealEditDto;
import pl.auctane.meal.entities.Meal;
import pl.auctane.meal.repositories.MealRepository;

import java.util.List;
import java.util.Optional;

@Service
public class MealService {
    private final MealRepository mealRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public MealService(MealRepository mealRepository, ObjectMapper objectMapper) {
        this.mealRepository = mealRepository;
        this.objectMapper = objectMapper;
    }

    public List<Meal> getMeals() {
        return mealRepository.findAll();
    }

    public Optional<Meal> getMeal(int id) {
        return mealRepository.findById(id);
    }

    public void createMeal(String name, String description) {
        Meal meal = new Meal();
        meal.setName(name);
        meal.setDescription(description);

        mealRepository.save(meal);
    }

    public void updateMeal(Meal meal) {
        mealRepository.save(meal);
    }

    public boolean deleteMeal(int id) {
        if(getMeal(id).isEmpty()) return false;

        mealRepository.deleteById(id);
        return true;
    }
}
