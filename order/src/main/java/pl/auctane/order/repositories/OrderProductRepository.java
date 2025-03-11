package pl.auctane.order.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.auctane.order.entities.OrderProduct;

import java.util.List;

public interface OrderProductRepository  extends JpaRepository<OrderProduct, Long> {
    List<OrderProduct> findAllByOrder_Id(Long orderId);
}
