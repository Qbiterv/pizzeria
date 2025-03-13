package pl.auctane.mail.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.bind.annotation.*;
import pl.auctane.mail.dtos.EmailDto;
import pl.auctane.mail.dtos.HtmlFileDto;
import pl.auctane.mail.services.EmailService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/v1/email")
public class SenderController {
    private final EmailService emailService;

    @Value("${spring.mail.username}")
    private String username;
    @Value("${spring.mail.html-file-path}")
    private String htmlFilePath;
    @Value("${spring.mail.image-file-path")
    private String imageFilePath;
    @Value("${spring.mail.image-2-file-path")
    private String image2FilePath;
    @Value("${spring.mail.image-file-name")
    private String imageFileName;
    @Value("${spring.mail.image-2-file-name")
    private String image2FileName;


    @Autowired
    public SenderController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PutMapping("/send/email={email}")
    public ResponseEntity<?> sendEmail(@PathVariable("email") String email) {

        emailService.sendEmail(username, email, "test", "test");

        System.out.println("Sent email to " + email);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/send-html")
    public ResponseEntity<?> sendHtml(@RequestBody EmailDto emailData) {

        List<HtmlFileDto> fileDtoList = new ArrayList<>();
        fileDtoList.add(new HtmlFileDto(imageFileName, new File(imageFilePath)));
        fileDtoList.add(new HtmlFileDto(image2FileName, new File(image2FilePath)));

        emailService.sendHtmlEmail(username, emailData, htmlFilePath, fileDtoList);

        System.out.println("Sent email with html file");

        return ResponseEntity.ok().build();
    }
}
