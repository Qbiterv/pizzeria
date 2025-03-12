package pl.auctane.order.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.auctane.order.entities.Order;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByOrderByIdAsc();

    List<Order> findAllByFinalizedTrue();

    List<Order> findAllByFinalizedFalse();

    List<Order> findAllByEmail(String email);
}
