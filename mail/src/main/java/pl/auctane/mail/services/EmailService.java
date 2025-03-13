package pl.auctane.mail.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import pl.auctane.mail.controllers.SenderController;
import pl.auctane.mail.dtos.*;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.service-url}")
    String serviceUrl;

    public void sendEmail(String from, String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }

    public void sendHtmlEmail(String from, EmailDto emailDto, String htmlPath, List<HtmlFileDto> files) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message);

        try {

            messageHelper.setFrom(from);
            messageHelper.setTo(emailDto.getTo());
            messageHelper.setSubject(emailDto.getSubject());

            try(var inputStream = SenderController.class.getResourceAsStream(htmlPath)) {
                assert inputStream != null : "HTML file not found in: " + htmlPath;

                //read html
                String html = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                //put user info into html
                html = PutDataIntoHtml(html, emailDto);
                //put html into mail
                messageHelper.setText(html, true);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            //put all attachments into mail
//            for (HtmlFileDto file : files) {
//                messageHelper.addAttachment(file.getFilename(), file.getFile());
//            }

            messageHelper.addAttachment("metapack.svg", new File("/static/metapack.svg"));
            messageHelper.addAttachment("auctane.jpg", new File("/static/auctane.jpg"));

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private String PutDataIntoHtml(String html, EmailDto emailDto) {
        HashMap<ProductDto, Integer> products = getProductsWithQuantity(emailDto.getProducts());

        // Make product list as one string
        StringBuilder productsAsString = new StringBuilder();

        for (Map.Entry<ProductDto, Integer> productAndQuantity : products.entrySet()) {
            String mealList = getMealList(productAndQuantity.getKey());

            String productInfo = """
                <li class="list-element">
                    <div class="list-element-container">
                        <p class="product-info">
                            <span class="quantity">%sx</span><span class="product-name">%s</span><span class="price">%s zł</span>
                        </p>
                        <p class="meal-list">%s</p>
                    </div>
                </li>
                """;

            // Adding info to product list
            productsAsString.append(String.format(productInfo, productAndQuantity.getValue(), productAndQuantity.getKey().getName(), productAndQuantity.getKey().getPrice(), mealList));
        }

        Object[] data = getObjects(emailDto, productsAsString);

        return String.format(html, data);
    }

    private Object[] getObjects(EmailDto emailDto, StringBuilder productsAsString) {
        double finalPrice = getFinalPrice(emailDto.getProducts());

        // Put all data into list
        return new Object[] {
                emailDto.getName(),
                emailDto.getSurname(),
                emailDto.getOrderId(),
                productsAsString.toString(),
                emailDto.getName(),
                emailDto.getSurname(),
                emailDto.getPhone(),
                emailDto.getAddress(),
                emailDto.getProducts().length,
                finalPrice
        };
    }

    private double getFinalPrice(ProductDto[] products) {
        double finalPrice = 0;
        for (ProductDto product : products) {
            finalPrice += product.getPrice();
        }
        return finalPrice;
    }
    private HashMap<ProductDto, Integer> getProductsWithQuantity(ProductDto[] products) {

        HashMap<ProductDto, Integer> productsWithQuantity = new HashMap<>();

        for (ProductDto product : products) {
            //if product is in list increment quantity
            if (productsWithQuantity.containsKey(product)) {
                productsWithQuantity.compute(product, (k, quantity) -> {
                    if(quantity == null) throw new RuntimeException("Bad thing Integer is null, when it should not be");
                    return quantity + 1;
                });
                continue;
            }
            //add product
            productsWithQuantity.put(product, 1);
        }

        return productsWithQuantity;
    }
    private String getMealList(ProductDto product) {
        StringBuilder mealList = new StringBuilder();

        //get mealList via http
        ResponseEntity<MealListResponseDto> response = null;

        String url = serviceUrl + "/product-meal/product/" + product.getId();

        try {
            response = new RestTemplate().getForEntity(url, MealListResponseDto.class);
        } catch (HttpStatusCodeException | ResourceAccessException e) {
            throw new RuntimeException("Error while getting meal list. ", e);
        }

        MealDto[] meals = Objects.requireNonNull(response.getBody()).getMeals();

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Error while getting meal list. Status code of response: " + response.getStatusCode());
        }

        //if no meals
        if(meals.length == 0)
            return "";

        for(int i = 0; i < meals.length; i++) {
            if(i == 0)
                mealList.append(meals[i].getMeal().getName());
            else mealList.append(", ").append(meals[i].getMeal().getName());
        }

        return mealList.toString();
    }
}
