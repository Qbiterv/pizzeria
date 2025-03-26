package pl.auctane.mail.dtos.product;

import lombok.Getter;
import lombok.Setter;
import pl.auctane.mail.dtos.meal.MealWithQuantityDto;

import java.util.List;

@Getter
@Setter
public class ProductWithQuantityAndMealsDto {
    ProductDto product;
    List<MealWithQuantityDto> meals;
    int quantity;

    public ProductWithQuantityAndMealsDto(ProductDto product, List<MealWithQuantityDto> meals, int quantity) {
        this.product = product;
        this.meals = meals;
        this.quantity = quantity;
    }
}
