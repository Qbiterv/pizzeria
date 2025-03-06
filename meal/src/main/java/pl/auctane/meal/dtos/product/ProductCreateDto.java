package pl.auctane.meal.dtos.product;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductCreateDto {
    private String name;
    private String description;
    private int categoryId;
    private double price;
}
