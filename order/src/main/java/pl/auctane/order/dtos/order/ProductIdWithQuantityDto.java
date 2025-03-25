package pl.auctane.order.dtos.order;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProductIdWithQuantityDto {
    Long productId;
    int quantity;

    public ProductIdWithQuantityDto() {}
    public ProductIdWithQuantityDto(Long productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }
    public ProductWithQuantityAndMealsDto toProductWithQuantityAndMeals(ProductDto productDto, List<MealWithQuantityDto> meals) {
        return new ProductWithQuantityAndMealsDto(productDto, meals, quantity);
    }
}
