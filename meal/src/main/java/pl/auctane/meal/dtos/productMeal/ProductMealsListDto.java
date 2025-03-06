package pl.auctane.meal.dtos.productMeal;

import lombok.Getter;
import lombok.Setter;
import pl.auctane.meal.entities.Meal;

import java.util.List;
import java.util.stream.Collector;

@Getter
@Setter
public class ProductMealsListDto {
    private int id;
    private Meal meal;

    public ProductMealsListDto(int id, Meal meal) {
        this.id = id;
        this.meal = meal;
    }
}