package pl.auctane.mail.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProductWithQuantityDto {
    ProductDto product;
    List<MealWithQuantityDto> meals;
    int quantity;
}
