package pl.auctane.order.dtos.email;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class MailDto implements Serializable {
    private Long orderId;
    private String type;

    public MailDto(Long orderId, String type) {
        this.orderId = orderId;
        this.type = type;
    }
}
