package pl.auctane.mail.services;

import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import pl.auctane.mail.controllers.SenderController;
import pl.auctane.mail.dtos.email.EmailOrderDataDto;
import pl.auctane.mail.dtos.email.EmailProductData;
import pl.auctane.mail.dtos.email.EmailStatusDto;
import pl.auctane.mail.dtos.email.HtmlFileDto;
import pl.auctane.mail.dtos.meal.MealWithQuantityDto;
import pl.auctane.mail.dtos.order.OrderDto;
import pl.auctane.mail.dtos.order.OrderProductsDataDto;
import pl.auctane.mail.dtos.order.OrderStatusDto;
import pl.auctane.mail.dtos.product.ProductWithQuantityAndMealsDto;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class EmailService {

    private final OrderModuleService orderModuleService;
    private final ResourceLoader resourceLoader;
    private final JavaMailSender mailSender;

    @Autowired
    public EmailService(OrderModuleService orderModuleService, ResourceLoader resourceLoader, JavaMailSender mailSender) {
        this.orderModuleService = orderModuleService;
        this.resourceLoader = resourceLoader;
        this.mailSender = mailSender;
    }

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

    private HtmlFileDto image1;
    private HtmlFileDto image2;

    @PostConstruct
    public void init() {
        image1 = new HtmlFileDto(imageFileName, resourceLoader.getResource("classpath:" + imageFilePath));
        image2 = new HtmlFileDto(image2FileName, resourceLoader.getResource("classpath:" + image2FilePath));
    }

    //main methods
    public void sendOrderEmail(Long orderId) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = null;

        try {
            messageHelper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

            EmailOrderDataDto emailOrderDto = getEmailOrderDataDto(orderId);

            messageHelper.setFrom(username);
            messageHelper.setTo(emailOrderDto.getTo());
            messageHelper.setSubject(emailOrderDto.getSubject());

            //read html
            String html = getHtmlBody(orderHtmlPath);
            //put user info into html
            html = putOrderDataIntoHtml(html, emailOrderDto);
            //put html into mail
            messageHelper.setText(html, true);

            //put all attachments into mail
            List<HtmlFileDto> files = List.of(image1, image2);
            for (HtmlFileDto file : files)
                messageHelper.addInline(file.getFilename(), file.getFile());

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
    public void sendStatusEmail(Long orderId) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = null;

        try {
            messageHelper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

            EmailStatusDto emailDto = getEmailStatusDataDto(orderId);

            messageHelper.setFrom(username);
            messageHelper.setTo(emailDto.getTo());
            messageHelper.setSubject(emailDto.getSubject());

            //read html
            String html = getHtmlBody(statusHtmlPath);
            //put user info into html
            html = putStatusDataIntoHtml(html, emailDto);
            //put html into mail
            messageHelper.setText(html, true);

            //put all attachments into mail
            List<HtmlFileDto> files = List.of(image1, image2);
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

    private String putOrderDataIntoHtml(String html, EmailOrderDataDto emailOrderDataDto) {

        List<EmailProductData> productsWithData = getProductsWithDataFromProductsWithQuantity(emailOrderDataDto.getProductsWithQuantity());

        orderList(productsWithData);

        double fullPrice = getFullPrice(productsWithData);
        int productCount = getProductCount(productsWithData);

        String productsAsString = getProductListAsString(productsWithData);

        //put data into html
        HashMap<String, String> replaceValues = new HashMap<>();
        replaceValues.put("{name}", emailOrderDataDto.getName());
        replaceValues.put("{surname}", emailOrderDataDto.getSurname());
        replaceValues.put("{orderId}", String.valueOf(emailOrderDataDto.getOrderId()));
        replaceValues.put("{productList}", productsAsString);
        replaceValues.put("{phone}", emailOrderDataDto.getPhone());
        replaceValues.put("{address}", emailOrderDataDto.getAddress());
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

    private EmailOrderDataDto getEmailOrderDataDto(Long orderId){
//        return service.getOrder(orderId);
        EmailOrderDataDto emailOrderDataDto = new EmailOrderDataDto();

        //check if order exist
        Optional<OrderDto> otionalOrder = orderModuleService.getOrderById(orderId);
        if (otionalOrder.isEmpty())
            throw new IllegalArgumentException("Order with id " + orderId + " does not exist");


        OrderDto order = otionalOrder.get();

        emailOrderDataDto.setOrderId(orderId);
        emailOrderDataDto.setName(order.getName());
        emailOrderDataDto.setSurname(order.getSurname());
        emailOrderDataDto.setPhone(order.getPhone());
        emailOrderDataDto.setAddress(order.getAddress());
        emailOrderDataDto.setTo(order.getEmail());
        emailOrderDataDto.setSubject("Zamówienie zostało złożone - nr " + orderId);

        //get products with quantity
        Optional<OrderProductsDataDto> orderProductsData = orderModuleService.getOrderProductsData(orderId);
        if (orderProductsData.isEmpty())
            throw new IllegalArgumentException("Order with id " + orderId + " does not have any product data");

        emailOrderDataDto.setProductsWithQuantity(orderProductsData.get().getProductsWithQuantity());

        return emailOrderDataDto;
    }
    private EmailStatusDto getEmailStatusDataDto(Long orderId) {
        EmailStatusDto emailStatusDto = new EmailStatusDto();

        //check if order exist
        Optional<OrderDto> otionalOrder = orderModuleService.getOrderById(orderId);
        if (otionalOrder.isEmpty())
            throw new IllegalArgumentException("Order with id " + orderId + " does not exist");

        OrderDto order = otionalOrder.get();

        emailStatusDto.setOrderId(orderId);
        emailStatusDto.setName(order.getName());
        emailStatusDto.setSurname(order.getSurname());
        emailStatusDto.setTo(order.getEmail());
        emailStatusDto.setSubject("Zamówienie nr " + orderId + " zmieniło status");

        //get order status
        Optional<OrderStatusDto> orderStatus = orderModuleService.getOrderStatus(orderId);
        if (orderStatus.isEmpty())
            throw new IllegalArgumentException("Order with id " + orderId + " does not have status");

        emailStatusDto.setStatus(orderStatus.get().getName());

        return emailStatusDto;
    }

    //methods used in putOrderDataIntoHtml
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
