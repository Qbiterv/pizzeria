package pl.auctane.mail.dtos.order;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderDto {
    private Long id;
    private String name;
    private String surname;
    private String email;
    private String phone;
    private String address;

    public OrderDto(Long id, String name, String surname, String email, String phone, String address) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }
}
