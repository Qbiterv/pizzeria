package pl.auctane.meal.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.auctane.meal.entities.Product;
import pl.auctane.meal.entities.ProductMeal;

import java.util.List;

public interface ProductMealRepository extends JpaRepository<ProductMeal, Long> {
    List<ProductMeal> getAllByProductId(Product product);
}
