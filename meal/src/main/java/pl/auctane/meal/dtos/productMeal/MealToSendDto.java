package pl.auctane.meal.dtos.productMeal;

import lombok.Getter;
import lombok.Setter;
import pl.auctane.meal.entities.Meal;

import java.util.List;

@Getter
@Setter
public class MealToSendDto {
    private Long id;
    private Meal meal;

    public MealToSendDto(Long id, Meal meal) {
        this.id = id;
        this.meal = meal;
    }
}