package pl.auctane.order.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.auctane.order.dtos.status.StatusCreateDto;
import pl.auctane.order.entities.Status;
import pl.auctane.order.repositories.StatusRepository;

import java.util.List;
import java.util.Optional;

import java.util.List;

@Service
public class StatusService {
    private final StatusRepository statusRepository;

    @Autowired
    public StatusService(StatusRepository statusRepository) {
        this.statusRepository = statusRepository;
    }

    public List<Status> getAllStatuses() {
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

    public void deleteStatusById(Long id) {
        statusRepository.deleteById(id);
    }

    public void createStatus(StatusCreateDto statusCreateDto) {
        Status status = new Status();
        status.setState(statusCreateDto.getState());
        status.setName(statusCreateDto.getName());
        status.setType(statusCreateDto.getType());

        statusRepository.save(status);
    }

    public void updateStatus(Status status) {
        statusRepository.save(status);
    }

    public Optional<Status> getFirst() {
        return statusRepository.findFirstByOrderByStateAsc();
    }
}
