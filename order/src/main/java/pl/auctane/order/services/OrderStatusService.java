package pl.auctane.order.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.auctane.order.repositories.OrderStatusRepository;

@Service
public class OrderStatusService {
    private final OrderStatusRepository orderStatusRepository;

    @Autowired
    public OrderStatusService(OrderStatusRepository orderStatusRepository) {this.orderStatusRepository = orderStatusRepository;}
}
