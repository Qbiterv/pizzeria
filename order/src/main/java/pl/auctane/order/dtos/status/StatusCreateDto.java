package pl.auctane.order.dtos.status;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import pl.auctane.order.enums.StatusType;

@Getter
@Setter
public class StatusCreateDto {
    @NotNull(message = "State is mandatory")
    @Positive(message = "State must be greater than 0")
    private int state;

    @NotEmpty(message = "Name is mandatory")
    private String name;

    @NotNull(message = "Type is mandatory (CREATED / PRODUCTION / DELIVERY / COMPLETED)")
    private StatusType type;
}
