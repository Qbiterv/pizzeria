package pl.auctane.order.services;

import org.springframework.beans.factory.annotation.Autowired;
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
        orderProductRepository.findAllByOrder_Id(orderId).forEach(OrderProduct -> productIds.add(OrderProduct.getProductId()));
        return productIds;
    }

    public void createOrderProduct(Order order, Long productId) {
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setOrder(order);
        orderProduct.setProductId(productId);
        orderProductRepository.save(orderProduct);
    }

    public void createOrderProduct(OrderProduct orderProduct) {
        orderProductRepository.save(orderProduct);
    }
}
