package pl.auctane.order.dtos.status;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatusCreateDto {
    @NotNull(message = "State is mandatory")
    @Positive(message = "State must be greater than 0")
    private int state;

    @NotEmpty(message = "Name is mandatory")
    private String name;
}
