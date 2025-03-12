package pl.auctane.order.dtos.status;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatusPatchDto {
    private int state;
    private String name;
}
