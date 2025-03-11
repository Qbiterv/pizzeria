package pl.auctane.order.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.auctane.order.entities.Status;
import pl.auctane.order.repositories.StatusRepository;

import java.util.List;
import java.util.Optional;

@Service
public class StatusService {
    private final StatusRepository statusRepository;

    @Autowired
    public StatusService(StatusRepository statusRepository) {
        this.statusRepository = statusRepository;
    }

    public List<Status> getStatuses() {
        return statusRepository.findAll();
    }

    public Optional<Status> getStatusById(Long id) {
        return statusRepository.findById(id);
    }

    public Optional<Status> getStatusByName(String name) {
        return statusRepository.getFirstByName(name);
    }

    public Optional<Status> getStatusByState(int state) {
        return statusRepository.getStatusByState(state);
    }

    public void createStatus(int state, String name) {
        Status status = new Status();
        status.setState(state);
        status.setName(name);

        statusRepository.save(status);
    }
}
