package pl.auctane.mail.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.bind.annotation.*;
import pl.auctane.mail.services.EmailService;

@RestController
@RequestMapping("/v1/email")
public class SenderController {
    private final EmailService emailService;

    @Autowired
    public SenderController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PutMapping("/send/email={email}")
    public ResponseEntity<?> sendEmail(@PathVariable("email") String email) {

        emailService.sendEmail(email, "test", "test");

        System.out.println("Sent email to " + email);

        return  ResponseEntity.ok().build();
    }
}
