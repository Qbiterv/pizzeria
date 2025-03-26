package pl.auctane.mail.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import pl.auctane.mail.dtos.order.OrderDto;
import pl.auctane.mail.dtos.order.OrderProductsDataDto;
import pl.auctane.mail.dtos.order.OrderStatusDto;

import java.util.Optional;

@Service
public class OrderModuleService {

    @Value("${service.order.url}")
    String orderServiceUrl;

    public Optional<OrderDto> getOrderById(Long orderId) {
        String url = orderServiceUrl + "/order/get/" + orderId;

        ResponseEntity<OrderDto> response = null;

        try {
            response = new RestTemplate().getForEntity(url, OrderDto.class);
        } catch (HttpStatusCodeException | ResourceAccessException e) {
            return Optional.empty();
        }

        return Optional.ofNullable(response.getBody());
    }

    public Optional<OrderProductsDataDto> getOrderProductsData(Long orderId) {
        String url = orderServiceUrl + "/order-product/get-order-products/" + orderId;

        ResponseEntity<OrderProductsDataDto> response = null;

        try {
            response = new RestTemplate().getForEntity(url, OrderProductsDataDto.class);
        } catch (HttpStatusCodeException | ResourceAccessException e) {
            return Optional.empty();
        }

        return Optional.ofNullable(response.getBody());
    }

    public Optional<OrderStatusDto> getOrderStatus(Long orderId) {
        String url = orderServiceUrl + "/order-status/get/" + orderId;

        ResponseEntity<OrderStatusDto> response = null;

        try {
            response = new RestTemplate().getForEntity(url, OrderStatusDto.class);
        } catch (HttpStatusCodeException | ResourceAccessException e) {
            return Optional.empty();
        }

        return Optional.ofNullable(response.getBody());
    }
}
