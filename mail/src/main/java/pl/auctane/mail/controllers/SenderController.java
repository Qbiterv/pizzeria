package pl.auctane.mail.controllers;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.auctane.mail.dtos.EmailOrderDto;
import pl.auctane.mail.dtos.EmailStatusDto;
import pl.auctane.mail.dtos.HtmlFileDto;
import pl.auctane.mail.services.EmailService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/v1/email")
public class SenderController {
    private final EmailService emailService;
    private final ResourceLoader resourceLoader;

    @Value("${service.mail.username}")
    private String username;
    @Value("${service.mail.html-order-file-path}")
    private String orderHtmlPath;
    @Value("${service.mail.html-status-file-path}")
    private String statusHtmlPath;
    @Value("${service.mail.image-file-path}")
    private String imageFilePath;
    @Value("${service.mail.image-2-file-path}")
    private String image2FilePath;
    @Value("${service.mail.image-file-name}")
    private String imageFileName;
    @Value("${service.mail.image-2-file-name}")
    private String image2FileName;

    private Resource imageFile;
    private Resource image2File;

    @Autowired
    public SenderController(EmailService emailService, ResourceLoader resourceLoader) {
        this.emailService = emailService;
        this.resourceLoader = resourceLoader;
    }

    @PostConstruct
    public void init() {
        imageFile = resourceLoader.getResource("classpath:" + imageFilePath);
        image2File = resourceLoader.getResource("classpath:" + image2FilePath);
    }

    @PutMapping("/send/email={email}")
    public ResponseEntity<?> sendEmail(@PathVariable("email") String email) {

        emailService.sendEmail(username, email, "test", "test");

        System.out.println("Sent email to " + email);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/send-order")
    public ResponseEntity<?> sendHtml(@RequestBody EmailOrderDto payload) {

        List<HtmlFileDto> fileDtoList = new ArrayList<>();
        fileDtoList.add(new HtmlFileDto(imageFileName, imageFile));
        fileDtoList.add(new HtmlFileDto(image2FileName, image2File));

        try {
            emailService.sendOrderEmail(username, payload, orderHtmlPath, fileDtoList);
        }catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.ok().build();
    }

    @PostMapping("/send-status")
    public ResponseEntity<?> sendStatus(@RequestBody EmailStatusDto payload) {
        List<HtmlFileDto> fileDtoList = new ArrayList<>();
        fileDtoList.add(new HtmlFileDto(imageFileName, imageFile));
        fileDtoList.add(new HtmlFileDto(image2FileName, image2File));

        try {
            emailService.sendStatusEmail(username, payload, statusHtmlPath, fileDtoList);
        }catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.ok().build();
    }
}
