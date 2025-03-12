package pl.auctane.order.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.auctane.order.entities.OrderStatus;

import java.util.Optional;

public interface OrderStatusRepository  extends JpaRepository<OrderStatus, Long> {
    Optional<OrderStatus> findByOrder_Id(Long orderId);
}
