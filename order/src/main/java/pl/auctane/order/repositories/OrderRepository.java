package pl.auctane.order.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.auctane.order.entities.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}
