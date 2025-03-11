package pl.auctane.order.dtos.order;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderDto {
    private String name;
    private String surname;
    private String email;
    private String phone;
    private String address;
    private List<Long> products;
}
