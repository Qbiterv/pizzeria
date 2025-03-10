package pl.auctane.order.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.auctane.order.entities.Status;

@Repository
public interface StatusRepository extends JpaRepository<Status, Long> {
}
