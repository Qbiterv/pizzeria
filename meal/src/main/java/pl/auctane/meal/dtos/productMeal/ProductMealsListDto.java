package pl.auctane.meal.dtos.productMeal;

import lombok.Getter;
import lombok.Setter;
import pl.auctane.meal.entities.Meal;

import java.util.List;
import java.util.stream.Collector;

@Getter
@Setter
public class ProductMealsListDto {
    private Long id;
    private Meal meal;

    public ProductMealsListDto(Long id, Meal meal) {
        this.id = id;
        this.meal = meal;
    }
}