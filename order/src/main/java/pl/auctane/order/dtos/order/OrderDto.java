package pl.auctane.order.dtos.order;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderDto {
    @NotBlank(message = "Name is mandatory")
    private String name;

    @NotBlank(message = "Surname is mandatory")
    private String surname;

    @Email(message = "Email should be valid")
    @Size(max = 255, message = "Email should be less than 255 characters")
    private String email;

    @NotBlank(message = "Phone is mandatory")
    private String phone;

    @NotBlank(message = "Address is mandatory")
    private String address;

    @NotEmpty(message = "Products list cannot be empty")
    private List<ProductIdWithQuantityDto> products;
}
