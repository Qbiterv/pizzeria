package pl.auctane.mail.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductWithQuantityDto {
    ProductDto product;
    int quantity;
}
