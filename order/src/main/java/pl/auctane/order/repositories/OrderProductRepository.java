package pl.auctane.order.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.auctane.order.entities.OrderProduct;

public interface OrderProductRepository  extends JpaRepository<OrderProduct, Long> {
}
