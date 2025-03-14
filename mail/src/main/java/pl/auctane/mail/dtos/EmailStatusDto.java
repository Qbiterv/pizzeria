package pl.auctane.mail.dtos;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailStatusDto {
    private String to;
    private String subject;

    private String name;
    private String surname;
    private String status;
    private Long orderId;
}
