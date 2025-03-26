package pl.auctane.meal.dtos.meal;

import lombok.Getter;
import lombok.Setter;
import pl.auctane.meal.entities.Meal;

@Getter
@Setter
public class MealWithQuantityDto {
    private Meal meal;
    private int quantity;

    public MealWithQuantityDto(Meal meal, int quantity) {
        this.meal = meal;
        this.quantity = quantity;
    }
}
