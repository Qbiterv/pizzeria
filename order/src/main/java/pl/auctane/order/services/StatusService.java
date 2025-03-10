package pl.auctane.order.services;

import org.springframework.stereotype.Service;
import pl.auctane.order.repositories.StatusRepository;

@Service
public class StatusService {
    private final StatusRepository statusRepository;

    public StatusService(StatusRepository statusRepository) {
        this.statusRepository = statusRepository;
    }
}
