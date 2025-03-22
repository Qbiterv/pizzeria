package pl.auctane.meal.dtos.ProductCategory;

import lombok.Getter;
import lombok.Setter;
import pl.auctane.meal.entities.Category;
import pl.auctane.meal.entities.Product;

import java.util.List;

@Getter
@Setter
public class ProductWithCategories {
    private Product product;
    private List<Category> categories;

    public ProductWithCategories(Product product, List<Category> categoriesFromProductId) {
        this.product = product;
        this.categories = categoriesFromProductId;
    }
}
