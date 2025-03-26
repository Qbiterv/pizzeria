package pl.auctane.order.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pl.auctane.order.dtos.status.StatusCreateDto;
import pl.auctane.order.entities.Status;
import pl.auctane.order.enums.StatusType;
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
        return statusRepository.findAll(Sort.by(Sort.Direction.ASC, "state"));

    }

    public List<Status> getAllStatusesWithoutCanceled() {
        return statusRepository.findAllByTypeNot(StatusType.CANCELED, Sort.by(Sort.Direction.ASC, "state"));
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

    public Optional<Status> getFirstStatusOfType(StatusType type) {
        return statusRepository.findFirstByTypeOrderByStateAsc(type);
    }

    public Optional<Status> getCanceledStatus() {
        return statusRepository.getFirstByType(StatusType.CANCELED);
    }

    public List<String> getAllStatusesNamesWithoutCanceled() {
        return statusRepository.findAllByTypeNot(StatusType.CANCELED, Sort.by(Sort.Direction.ASC, "state")).stream().map(Status::getName).toList();
    }
}
