package pl.auctane.meal.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.auctane.meal.entities.MealCategory;

public interface MealCategoryRepository extends JpaRepository<MealCategory, Long> {
}
