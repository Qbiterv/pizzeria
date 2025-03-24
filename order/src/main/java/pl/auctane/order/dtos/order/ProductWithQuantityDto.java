package pl.auctane.order.dtos.order;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductWithQuantityDto {
    ProductDto product;
    int quantity;

    public ProductWithQuantityDto(ProductDto product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }
}

