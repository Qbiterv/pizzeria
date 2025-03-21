package pl.auctane.meal.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.auctane.meal.entities.ProductCategory;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {
}
