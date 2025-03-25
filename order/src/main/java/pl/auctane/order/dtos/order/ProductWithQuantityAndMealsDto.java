package pl.auctane.order.dtos.order;

import lombok.Getter;
import lombok.Setter;

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
