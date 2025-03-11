package pl.auctane.order.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import pl.auctane.order.entities.Order;
import pl.auctane.order.entities.OrderProduct;
import pl.auctane.order.repositories.OrderProductRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderProductService {
    private final OrderProductRepository orderProductRepository;

    @Autowired
    public OrderProductService(OrderProductRepository orderProductRepository) {this.orderProductRepository = orderProductRepository;}

    public List<OrderProduct> getAllOrderProducts() {
        return orderProductRepository.findAll();
    }

    public List<Long> getAllProductIdsForOrder(Long orderId) {
        List<Long> productIds = new ArrayList<>();
        orderProductRepository.findAllByOrder_Id(orderId).forEach(OrderProduct -> productIds.add(OrderProduct.getProduct()));
        return productIds;

        /*
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<List<String>> request = new HttpEntity<>(headers);

        String url = "http://localhost:8081/v1/product/get" + orderId;
        */
    }

    public void createOrderProduct(OrderProduct orderProduct) {
        orderProductRepository.save(orderProduct);
    }
}
