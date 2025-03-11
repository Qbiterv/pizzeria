package pl.auctane.order.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.auctane.order.entities.OrderStatus;

public interface OrderStatusRepository  extends JpaRepository<OrderStatus, Long> {
}
