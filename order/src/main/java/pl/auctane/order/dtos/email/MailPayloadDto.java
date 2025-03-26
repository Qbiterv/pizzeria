package pl.auctane.order.dtos.email;

import lombok.Getter;
import lombok.Setter;
import pl.auctane.order.dtos.product.ProductWithQuantityAndMealsDto;

import java.util.List;

@Getter
@Setter
public class MailPayloadDto {
    private String to;
    private String subject;
    private String name;
    private String surname;
    private String phone;
    private Long orderId;
    private String address;
    private List<ProductWithQuantityAndMealsDto> productsWithQuantity;
}
