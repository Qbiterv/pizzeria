package pl.auctane.order.dtos.email;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MailStatusDto {
    private String to;
    private String subject;

    private String name;
    private String surname;
    private String status;
    private Long orderId;
}
