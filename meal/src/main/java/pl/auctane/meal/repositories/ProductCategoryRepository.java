package pl.auctane.meal.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.auctane.meal.entities.Product;
import pl.auctane.meal.entities.ProductCategory;

import java.util.Collection;
import java.util.List;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {
    List<ProductCategory> findAllByCategory_Id(Long categoryId);

    List<ProductCategory> findAllByProduct_Id(Long productId);
}
