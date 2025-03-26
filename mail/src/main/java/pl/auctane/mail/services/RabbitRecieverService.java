package pl.auctane.mail.services;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.auctane.mail.dtos.email.MailDto;

import java.util.Objects;

@Service
public class RabbitRecieverService {

    private final EmailService emailService;

    @Autowired
    public RabbitRecieverService(EmailService emailService) {
        this.emailService = emailService;
    }

    @RabbitListener(queues = "mail.send")
    public void sendEmail(MailDto mailDto) {
        if(Objects.equals(mailDto.getType(), "order")) {
            emailService.sendOrderEmail(mailDto.getOrderId());
            return;
        }
        if(Objects.equals(mailDto.getType(), "status")) {
            emailService.sendStatusEmail(mailDto.getOrderId());
            return;
        }
    }
}
