package pl.auctane.order.services;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import pl.auctane.order.dtos.email.MailDto;

@Service
public class RabbitSenderService {

    private final RabbitTemplate rabbitTemplate;
    private static final String EXCHANGE_NAME = "mail-exchange";

    public RabbitSenderService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendEmail(MailDto mailDto) {
        rabbitTemplate.convertAndSend(EXCHANGE_NAME, "mail.send", mailDto);
    }
}
