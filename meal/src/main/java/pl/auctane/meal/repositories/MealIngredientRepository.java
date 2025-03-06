package pl.auctane.meal.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.auctane.meal.entities.MealIngredient;

import java.util.List;

@Repository
public interface MealIngredientRepository extends JpaRepository<MealIngredient, Integer> {
    List<MealIngredient> findAllByMealId_Id(int mealIdId);
}
