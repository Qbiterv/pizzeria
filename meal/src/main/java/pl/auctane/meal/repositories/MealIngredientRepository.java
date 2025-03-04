package pl.auctane.meal.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.auctane.meal.entities.MealIngredient;

@Repository
public interface MealIngredientRepository extends JpaRepository<MealIngredient, Integer> {
}
