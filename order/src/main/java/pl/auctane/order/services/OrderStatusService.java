package pl.auctane.order.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.auctane.order.entities.Order;
import pl.auctane.order.entities.OrderStatus;
import pl.auctane.order.entities.Status;
import pl.auctane.order.enums.StatusType;
import pl.auctane.order.repositories.OrderStatusRepository;

import java.util.Optional;

@Service
public class OrderStatusService {
    private final OrderStatusRepository orderStatusRepository;
    private final StatusService statusService;

    @Autowired
    public OrderStatusService(OrderStatusRepository orderStatusRepository, StatusService statusService) {this.orderStatusRepository = orderStatusRepository; this.statusService = statusService;}

    public Object getAllOrderStatuses() {
        return orderStatusRepository.findAll();
    }

    public Optional<OrderStatus> getOrderStatus(Long orderId) {
        return orderStatusRepository.findByOrder_Id(orderId);
    }

    public boolean registerOrder(Order order) {
        Optional<Status> status = statusService.getFirst();

        if(status.isEmpty()) {
            System.out.println("tried to register order but no status has been found");
            return false;
        }

        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrder(order);
        orderStatus.setStatus(status.get());

        orderStatusRepository.save(orderStatus);
        return true;
    }

    public void updateOrderStatus(OrderStatus orderStatus, Status status) {
        orderStatus.setStatus(status);
        orderStatusRepository.save(orderStatus);
    }
}
