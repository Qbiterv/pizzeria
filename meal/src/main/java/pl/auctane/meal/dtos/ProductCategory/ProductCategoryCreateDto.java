package pl.auctane.meal.dtos.ProductCategory;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductCategoryCreateDto {
    private Long productId;
    private Long categoryId;
}
