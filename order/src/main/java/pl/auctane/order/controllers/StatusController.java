package pl.auctane.order.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import pl.auctane.order.dtos.status.StatusCreateDto;
import pl.auctane.order.entities.Status;
import pl.auctane.order.services.StatusService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/status")
public class StatusController {
    private final ObjectMapper objectMapper;
    private final StatusService statusService;

    @Autowired
    public StatusController(ObjectMapper objectMapper, StatusService statusService) {
        this.objectMapper = objectMapper;
        this.statusService = statusService;
    }

    @GetMapping("/get")
    public ResponseEntity<?> getStatuses() {
        return ResponseEntity.ok().body(statusService.getStatuses());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getStatusById(@PathVariable("id") Long id) {
        Optional<Status> status = statusService.getStatusById(id);

        if(status.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok().body(status.get());
    }

    @GetMapping("/get/name/{name}")
    public ResponseEntity<?> getStatusByName(@PathVariable("name") String name) {
        Optional<Status> status = statusService.getStatusByName(name);

        if(status.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok().body(status.get());
    }

    @GetMapping("/get/state/{state}")
    public ResponseEntity<?> getStatusByState(@PathVariable("state") int state) {
        Optional<Status> status = statusService.getStatusByState(state);

        if(status.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok().body(objectMapper.convertValue(status, Status.class));
    }

    @PostMapping(value = "/create", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> createStatus(@Valid @RequestBody StatusCreateDto statusCreateDto, BindingResult bindingResult) {
        ObjectNode JSON = objectMapper.createObjectNode();

        // Bean validation
        if (bindingResult.hasErrors()) {
            JSON.put("success", false);
            JSON.put("message", bindingResult.getAllErrors().stream().map(e -> ((FieldError) e).getField() + " " + e.getDefaultMessage()).collect(Collectors.joining(", ")));

            return ResponseEntity.badRequest().body(JSON);
        }

        if(statusService.getStatusByState(statusCreateDto.getState()).isPresent()) {
            JSON.put("success", false);
            JSON.put("message", "Status with state " + statusCreateDto.getState() + " already exists");

            return ResponseEntity.badRequest().body(JSON);
        }

        statusService.createStatus(statusCreateDto.getState(), statusCreateDto.getName());

        JSON.put("success", true);
        JSON.put("message", "Status " + statusCreateDto.getName() + " created successfully | Priority: " + statusCreateDto.getState());

        return ResponseEntity.ok().body(JSON);
    }

}
