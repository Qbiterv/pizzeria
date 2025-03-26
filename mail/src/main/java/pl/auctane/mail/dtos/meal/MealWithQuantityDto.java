package pl.auctane.mail.dtos.meal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MealWithQuantityDto {
    private MealDto meal;
    private int quantity;

    public MealWithQuantityDto(MealDto meal, int quantity) {
        this.meal = meal;
        this.quantity = quantity;
    }
}
