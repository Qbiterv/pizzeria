package pl.auctane.meal.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.auctane.meal.entities.ProductMeal;

public interface ProductMealRepository extends JpaRepository<ProductMeal, Integer> {
}
