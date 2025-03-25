package pl.auctane.order.dtos;

import lombok.Getter;
import lombok.Setter;
import pl.auctane.order.dtos.order.ProductWithQuantityAndMealsDto;
import pl.auctane.order.entities.OrderProduct;

import java.util.List;

@Getter
@Setter
public class OrderProductsDto {
    List<ProductWithQuantityAndMealsDto> productsWithQuantity;

    public OrderProductsDto(List<ProductWithQuantityAndMealsDto> productsWithQuantity) {
        this.productsWithQuantity = productsWithQuantity;
    }
}
