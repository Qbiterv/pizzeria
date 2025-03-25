package pl.auctane.mail.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import pl.auctane.mail.controllers.SenderController;
import pl.auctane.mail.dtos.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

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

        List<EmailProductData> productsWithData = getProductsWithDataFromProductsWithQuantity(emailOrderDto.getProductsWithQuantity());

        orderList(productsWithData);

        double fullPrice = getFullPrice(productsWithData);
        int productCount = getProductCount(productsWithData);

        String productsAsString = getProductListAsString(productsWithData);

        //put data into html
        HashMap<String, String> replaceValues = new HashMap<>();
        replaceValues.put("{name}", emailOrderDto.getName());
        replaceValues.put("{surname}", emailOrderDto.getSurname());
        replaceValues.put("{orderId}", String.valueOf(emailOrderDto.getOrderId()));
        replaceValues.put("{productList}", productsAsString);
        replaceValues.put("{phone}", emailOrderDto.getPhone());
        replaceValues.put("{address}", emailOrderDto.getAddress());
        replaceValues.put("{finalPrice}", String.format("%.2f", fullPrice));
        replaceValues.put("{productCount}", String.valueOf(productCount));

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

    private String getProductListAsString(List<EmailProductData> productsData) {
        StringBuilder productsAsString = new StringBuilder();

        for (EmailProductData productData : productsData) {

            String productInfo = """
                <li class="list-element">
                    <div class="list-element-container">
                        <p class="product-info">
                            <span class="quantity">%dx</span><span class="product-name">%s</span><span class="price">%.2f zł</span>
                        </p>
                        <p class="meal-list">%s</p>
                    </div>
                </li>
                """;

            productsAsString.append(String.format(productInfo, productData.getQuantity(), productData.getProduct().getName(), productData.getPrice(), productData.getMeals()));
        }

        return productsAsString.toString();
    }
    private void orderList(List<EmailProductData> productsWithData){
        //if only one product in list return
        if (productsWithData.size() == 1) return;

        for (int i = 1; i < productsWithData.size(); i++) {
            EmailProductData current = productsWithData.get(i);
            int j =  i - 1;

            while (j >= 0 && shouldProductBeLower(current, productsWithData.get(j))) {
                productsWithData.set(j + 1, productsWithData.get(j));
                j--;
            }

            productsWithData.set(j + 1, current);
        }
    }
    private boolean shouldProductBeLower(EmailProductData current, EmailProductData target) {
        //if product is a kit and current is not move || if product is more expensive than current move
        return !current.getMeals().isEmpty() && target.getMeals().isEmpty() || current.getPrice() > target.getPrice();
    }
    private double getFullPrice(List<EmailProductData> productsData) {
        double finalPrice = 0;
        for (EmailProductData productData : productsData) {
            finalPrice += productData.getPrice();
        }
        return finalPrice;
    }
    private int getProductCount(List<EmailProductData> productsData) {
        int productCount = 0;
        for (EmailProductData productData : productsData) {
            productCount += productData.getQuantity();
        }
        return productCount;
    }
    private List<EmailProductData> getProductsWithDataFromProductsWithQuantity(List<ProductWithQuantityAndMealsDto> productsWithQuantity) {

        List<EmailProductData> productsAndData = new ArrayList<>();

        for (ProductWithQuantityAndMealsDto product : productsWithQuantity) {
            productsAndData.add(new EmailProductData(product.getProduct(), product.getQuantity(), product.getProduct().getPrice() * product.getQuantity(), makeMealList(product.getMeals())));
        }

        return productsAndData;
    }
    private String makeMealList(List<MealWithQuantityDto> meals) {

        //only one meal
        if (meals.isEmpty()) return "";

        StringBuilder mealList = new StringBuilder();

        for (MealWithQuantityDto mealAndQuantity : meals) {
            int quantity = mealAndQuantity.getQuantity();

            if(quantity > 1)
                mealList.append(quantity).append("x ").append(mealAndQuantity.getMeal().getName()).append(", ");
            else
                mealList.append(mealAndQuantity.getMeal().getName()).append(", ");
        }

        //delete last comma
        mealList.setLength(mealList.length() - 2);

        return mealList.toString();
    }
}
