package pl.auctane.order.dtos.order;

import lombok.Getter;
import lombok.Setter;
import pl.auctane.order.dtos.product.ProductWithQuantityAndMealsDto;

import java.util.List;

@Getter
@Setter
public class OrderProductsDto {
    List<ProductWithQuantityAndMealsDto> productsWithQuantity;

    public OrderProductsDto(List<ProductWithQuantityAndMealsDto> productsWithQuantity) {
        this.productsWithQuantity = productsWithQuantity;
    }
}
