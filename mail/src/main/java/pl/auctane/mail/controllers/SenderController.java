package pl.auctane.mail.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PutMapping("/send-order/{id}")
    public ResponseEntity<?> sendHtml(@PathVariable("id") Long orderId) {
        try {
            emailService.sendOrderEmail(orderId);
        }catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.ok().build();
    }

    @PutMapping("/send-status/{id}")
    public ResponseEntity<?> sendStatus(@PathVariable("id") Long orderId) {
        try {
            emailService.sendStatusEmail(orderId);
        }catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.ok().build();
    }
}
