package pl.auctane.meal.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.auctane.meal.entities.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {
}
