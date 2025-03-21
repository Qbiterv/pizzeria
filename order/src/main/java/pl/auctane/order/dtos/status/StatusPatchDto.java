package pl.auctane.order.dtos.status;

import lombok.Getter;
import lombok.Setter;
import pl.auctane.order.enums.StatusType;

@Getter
@Setter
public class StatusPatchDto {
    private int state;
    private String name;
    private StatusType type;
}
