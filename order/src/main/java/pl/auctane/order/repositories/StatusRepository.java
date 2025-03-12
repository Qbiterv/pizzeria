package pl.auctane.order.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.auctane.order.entities.Status;

import java.util.List;
import java.util.Optional;

@Repository
public interface StatusRepository extends JpaRepository<Status, Long> {
    Optional<Status> getStatusByState(int state);

    Optional<Status> getFirstByName(String name);

    Optional<Status> findFirstByOrderByStateAsc();
}
