package pl.auctane.meal.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.auctane.meal.entities.Meal;
import pl.auctane.meal.entities.MealCategory;
import pl.auctane.meal.repositories.MealCategoryRepository;

import java.util.List;
import java.util.Optional;

@Service
public class MealCategoryService {
    private final MealCategoryRepository mealCategoryRepository;
    private final MealService mealService;

    @Autowired
    public MealCategoryService(MealCategoryRepository mealCategoryRepository, MealService mealService) {
        this.mealCategoryRepository = mealCategoryRepository;
        this.mealService = mealService;
    }

    public List<MealCategory> findAll() {
        return mealCategoryRepository.findAll();
    }

    public Optional<MealCategory> findById(Long id) {
        return mealCategoryRepository.findById(id);
    }

    public void save(MealCategory mealCategory) {
        mealCategoryRepository.save(mealCategory);
    }

    public void deleteById(Long id) {
        mealCategoryRepository.deleteById(id);
    }
}
