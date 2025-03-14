package pl.auctane.mail.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${service-url}")
    String serviceUrl;

    public void sendEmail(String from, String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }

    public void sendOrderEmail(String from, EmailOrderDto emailOrderDto, String htmlPath, List<HtmlFileDto> files) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = null;

        try {
            messageHelper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

            messageHelper.setFrom(from);
            messageHelper.setTo(emailOrderDto.getTo());
            messageHelper.setSubject(emailOrderDto.getSubject());

            //read html
            String html = getHtmlBody(htmlPath);
            //put user info into html
            html = putOrderDataIntoHtml(html, emailOrderDto);
            //put html into mail
            messageHelper.setText(html, true);

            //put all attachments into mail
            for (HtmlFileDto file : files)
                messageHelper.addInline(file.getFilename(), file.getFile());

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
    public void sendStatusEmail(String from, EmailStatusDto emailDto, String htmlPath, List<HtmlFileDto> files) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = null;

        try {
            messageHelper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

            messageHelper.setFrom(from);
            messageHelper.setTo(emailDto.getTo());
            messageHelper.setSubject(emailDto.getSubject());

            //read html
            String html = getHtmlBody(htmlPath);
            //put user info into html
            html = putStatusDataIntoHtml(html, emailDto);
            //put html into mail
            messageHelper.setText(html, true);

            //put all attachments into mail
            for (HtmlFileDto file : files)
                messageHelper.addInline(file.getFilename(), file.getFile());

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private String getHtmlBody(String htmlPath) {
        String html = null;
        try (var inputStream = SenderController.class.getResourceAsStream(htmlPath)) {
            assert inputStream != null : "HTML file not found in: " + htmlPath;
            html = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Error while reading html. " + e);
        }
        return html;
    }
    private String putOrderDataIntoHtml(String html, EmailOrderDto emailOrderDto) {

        List<ProductDto> products = getProductsFromIds(emailOrderDto.getProductIds());

        HashMap<ProductDto, Integer> productsWithQuantity = getProductsWithQuantity(products);

        double finalPrice = getFinalPrice(products);

        String productsAsString = getProductListAsString(productsWithQuantity);

        //put data into html
        HashMap<String, String> replaceValues = new HashMap<>();
        replaceValues.put("{name}", emailOrderDto.getName());
        replaceValues.put("{surname}", emailOrderDto.getSurname());
        replaceValues.put("{orderId}", String.valueOf(emailOrderDto.getOrderId()));
        replaceValues.put("{productList}", productsAsString);
        replaceValues.put("{phone}", emailOrderDto.getPhone());
        replaceValues.put("{address}", emailOrderDto.getAddress());
        replaceValues.put("{finalPrice}", String.format("%.2f", finalPrice));
        replaceValues.put("{productCount}", String.valueOf(products.size()));

        String finalHtml = html;

        for (Map.Entry<String, String> entry : replaceValues.entrySet()) {
            html = html.replace(entry.getKey(), entry.getValue());
        }

        return html;
    }
    private String putStatusDataIntoHtml(String html, EmailStatusDto emailStatusDto) {

        HashMap<String, String> replaceValues = new HashMap<>();
        replaceValues.put("{name}", emailStatusDto.getName());
        replaceValues.put("{surname}", emailStatusDto.getSurname());
        replaceValues.put("{orderId}", String.valueOf(emailStatusDto.getOrderId()));
        replaceValues.put("{status}", emailStatusDto.getStatus());

        String finalHtml = html;

        for (Map.Entry<String, String> entry : replaceValues.entrySet()) {
            html = html.replace(entry.getKey(), entry.getValue());
        }

        return html;
    }

    private HashMap<ProductDto, Integer> getProductsWithQuantity(List<ProductDto> products) {

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
    private String getProductListAsString(HashMap<ProductDto, Integer> productsWithQuantity) {
        StringBuilder productsAsString = new StringBuilder();

        for (Map.Entry<ProductDto, Integer> productAndQuantity : productsWithQuantity.entrySet()) {
            String mealList = getMealListForProductId(productAndQuantity.getKey().getId());

            String productInfo = """
                <li class="list-element">
                    <div class="list-element-container">
                        <p class="product-info">
                            <span class="quantity">%sx</span><span class="product-name">%s</span><span class="price">%.2f zł</span>
                        </p>
                        <p class="meal-list">%s</p>
                    </div>
                </li>
                """;

            // Adding info to product list
            productsAsString.append(String.format(productInfo, productAndQuantity.getValue(), productAndQuantity.getKey().getName(), productAndQuantity.getKey().getPrice(), mealList));
        }

        return productsAsString.toString();
    }
    private double getFinalPrice(List<ProductDto> products) {
        double finalPrice = 0;
        for (ProductDto product : products) {
            finalPrice += product.getPrice();
        }
        return finalPrice;
    }
    private String getMealListForProductId(Long productId) {
        StringBuilder mealList = new StringBuilder();

        //get mealList via http
        ResponseEntity<MealListResponseDto> response = null;

        String url = serviceUrl + "/product-meal/product/" + productId;

        try {
            response = new RestTemplate().getForEntity(url, MealListResponseDto.class);
        } catch (HttpStatusCodeException | ResourceAccessException e) {
            throw new RuntimeException("Error while getting meal list. ", e);
        }

        //no meals
        if(response.getStatusCode() == HttpStatus.NO_CONTENT) return "";

        if (!response.getStatusCode().is2xxSuccessful())
            throw new RuntimeException("Error while getting meal list. Status code of response is not ok and not NO_CONTENT. Status code: " + response.getStatusCode());

        if(response.getBody() == null)
            throw new RuntimeException("Error while getting meal list. Body of response is null, but status code is ok");

        MealDto[] meals = response.getBody().getMeals();

        for(int i = 0; i < meals.length; i++) {
            if(i == 0)
                mealList.append(meals[i].getMeal().getName());
            else mealList.append(", ").append(meals[i].getMeal().getName());
        }

        return mealList.toString();
    }
    private List<ProductDto> getProductsFromIds(List<Long> ids) {
        //get product list via http

        List<ProductDto> products = new ArrayList<>();

        //call get product from id for each id
        for (Long id : ids) {
            String url = serviceUrl + "/product/get/" + id;

            ResponseEntity<ProductDto> response = new RestTemplate().getForEntity(url, ProductDto.class);

            try {
                response = new RestTemplate().getForEntity(url, ProductDto.class);;
            } catch (HttpStatusCodeException | ResourceAccessException e) {
                throw new RuntimeException("Error while getting product for email. ", e);
            }

            //no product
            if (response.getStatusCode() == HttpStatus.NO_CONTENT)
                throw new RuntimeException("Error while getting product for email. Product in order is pointing on product that doesn't exist");

            if (!response.getStatusCode().is2xxSuccessful())
                throw new RuntimeException("Error while getting product for email. Status code of response is not ok and not NO_CONTENT. Status code: " + response.getStatusCode());

            if (response.getBody() == null)
                throw new RuntimeException("Error while getting product for email. Body of response is null, but status code is ok");

            products.add(response.getBody());
        }

        return products;
    }
}
