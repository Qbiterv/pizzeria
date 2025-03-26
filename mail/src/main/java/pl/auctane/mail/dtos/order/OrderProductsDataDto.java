package pl.auctane.mail.dtos.order;

import lombok.Getter;
import lombok.Setter;
import pl.auctane.mail.dtos.product.ProductWithQuantityAndMealsDto;

import java.util.List;

@Getter
@Setter
public class OrderProductsDataDto {
    private List<ProductWithQuantityAndMealsDto> productsWithQuantity;
}
