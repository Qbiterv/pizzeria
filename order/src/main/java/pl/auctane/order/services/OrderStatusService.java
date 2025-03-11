package pl.auctane.order.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.auctane.order.entities.OrderStatus;
import pl.auctane.order.repositories.OrderStatusRepository;

import java.util.Optional;

@Service
public class OrderStatusService {
    private final OrderStatusRepository orderStatusRepository;

    @Autowired
    public OrderStatusService(OrderStatusRepository orderStatusRepository) {this.orderStatusRepository = orderStatusRepository;}

    public Object getAllOrderStatuses() {
        return orderStatusRepository.findAll();
    }

    public OrderStatus getOrderStatus(Long orderId) {
        return orderStatusRepository.findById(orderId).orElse(null);
    }
}
