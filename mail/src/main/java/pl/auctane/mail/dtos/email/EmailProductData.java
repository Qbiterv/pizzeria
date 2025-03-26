package pl.auctane.mail.dtos.email;

import lombok.Getter;
import lombok.Setter;
import pl.auctane.mail.dtos.product.ProductDto;

@Getter
@Setter
public class EmailProductData {
    ProductDto product;
    Integer quantity;
    double price;
    String meals;

    public EmailProductData(ProductDto product, Integer quantity, double price, String meals) {
        this.product = product;
        this.quantity = quantity;
        this.price = price;
        this.meals = meals;
    }

    public void incrementQuantity() {
        this.quantity++;
    }
    public void addPrice(double price) {
        this.price += price;
    }
}
