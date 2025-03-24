package pl.auctane.mail.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EmailOrderDto {
    private String to;
    private String subject;

    private String name;
    private String surname;
    private String phone;
    private Long orderId;
    private String address;
    private List<ProductWithQuantityDto> productsWithQuantity;
}
