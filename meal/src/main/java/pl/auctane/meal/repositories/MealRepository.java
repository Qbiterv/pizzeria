package pl.auctane.meal.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.auctane.meal.entities.Meal;

@Repository
public interface MealRepository extends JpaRepository<Meal, Long> {
}
