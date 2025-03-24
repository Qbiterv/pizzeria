package pl.auctane.order.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.auctane.order.dtos.order.ProductWithQuantityDto;
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

    public List<OrderProduct> getAllOrderProductsForOrder(Long orderId) {
        return orderProductRepository.findAllByOrder_Id(orderId);
    }

    public void createOrderProduct(Order order, Long productId, int quantity) {
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setOrder(order);
        orderProduct.setProductId(productId);
        orderProduct.setQuantity(quantity);
        orderProductRepository.save(orderProduct);
    }

    public void createOrderProduct(Order order, ProductWithQuantityDto productWithQuantity) {
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setOrder(order);
        orderProduct.setProductId(productWithQuantity.getProduct().getId());
        orderProduct.setQuantity(productWithQuantity.getQuantity());
        orderProductRepository.save(orderProduct);
    }
}
