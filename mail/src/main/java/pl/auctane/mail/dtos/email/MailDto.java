package pl.auctane.mail.dtos.email;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class MailDto implements Serializable {
    private Long orderId;
    private String type;
}
