package pl.auctane.mail.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/email")
public class SenderController {
    private final JavaMailSender mailSender = new JavaMailSenderImpl();

    @PutMapping("/send/email={email}")
    public ResponseEntity<?> sendEmail(@PathVariable("email") String email) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(email);
        message.setFrom("auctane@pl");
        message.setSubject("Test email");
        message.setText("This is a test email");
        mailSender.send(message);

        System.out.println("Sent email to " + email);

        return  ResponseEntity.ok().build();
    }
}
