package pl.auctane.meal.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.auctane.meal.entities.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
}
