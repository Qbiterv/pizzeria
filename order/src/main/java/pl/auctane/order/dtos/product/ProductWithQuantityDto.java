package pl.auctane.order.dtos.product;

import lombok.Getter;
import lombok.Setter;
import pl.auctane.order.dtos.meal.MealWithQuantityDto;

import java.util.List;

@Getter
@Setter
public class ProductWithQuantityDto {
    ProductDto product;
    int quantity;

    public ProductWithQuantityDto(ProductDto product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }
    public ProductWithQuantityAndMealsDto toProductWithQuantityAndMeals(List<MealWithQuantityDto> meals) {
        return new ProductWithQuantityAndMealsDto(product, meals, quantity);
    }
}

