package pl.auctane.mail.dtos.email;

import lombok.Getter;
import lombok.Setter;
import pl.auctane.mail.dtos.product.ProductWithQuantityAndMealsDto;

import java.util.List;

@Getter
@Setter
public class EmailOrderDataDto {
    private String to;
    private String subject;

    private String name;
    private String surname;
    private String phone;
    private Long orderId;
    private String address;
    private List<ProductWithQuantityAndMealsDto> productsWithQuantity;
}
