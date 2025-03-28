package pl.auctane.brandenburg.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import pl.auctane.brandenburg.services.SessionService;

import java.util.UUID;

@RestController
@RequestMapping("/session")
public class SessionController {

    private final SessionService sessionService;
    private final ObjectMapper objectMapper;

    public SessionController(SessionService sessionService, ObjectMapper objectMapper) {
        this.sessionService = sessionService;
        this.objectMapper = objectMapper;
    }

    @GetMapping
    public ResponseEntity<?> getAllSessions() {
        return ResponseEntity.ok().body(sessionService.getAllSessions());
    }
    @GetMapping("/check")
    public ResponseEntity<?> checkSession(@RequestHeader("Authorization") String authorization) {
        boolean isSessionActive = sessionService.isSessionActive(authorization);

        return (isSessionActive) ? ResponseEntity.ok().build() : ResponseEntity.status(401).build();
    }
    @GetMapping("/access")
    public ResponseEntity<?> checkAccess(@RequestHeader("Authorization") String authorization, ServerHttpRequest request) throws Exception {
        boolean hasUserAccess = sessionService.hasUserAccess(authorization, request);

        return (hasUserAccess) ? ResponseEntity.ok().build() : ResponseEntity.status(401).build();
    }

    @PostMapping("/create")
    public ResponseEntity<?> createSession(@RequestHeader("Authorization") String authorization, ServerHttpRequest request) {
        ObjectNode JSON = objectMapper.createObjectNode();

        if (authorization == null || authorization.isEmpty()) {
            JSON.put("success", false);
            JSON.put("message", "Authorization header is missing");
            return ResponseEntity.badRequest().body(JSON);
        }

        String sessionId = null;

        try {
            sessionId = sessionService.createSession(authorization, request);
        }catch (IllegalArgumentException e){
            JSON.put("success", false);
            JSON.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(JSON);
        }

        JSON.put("success", true);
        JSON.put("message", sessionId);
        return ResponseEntity.ok().body(JSON);
    }
    @DeleteMapping
    public ResponseEntity<?> destroySession(@RequestHeader("Authorization") String authorization, ServerHttpRequest request) {
        ObjectNode JSON = objectMapper.createObjectNode();

        if (authorization == null || authorization.isEmpty()) {
            JSON.put("success", false);
            JSON.put("message", "Authorization header is missing");
            return ResponseEntity.badRequest().body(JSON);
        }

        try{
            sessionService.removeSession(authorization, request);
        } catch (Exception e) {
            JSON.put("success", false);
            JSON.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(JSON);
        }

        JSON.put("success", true);
        JSON.put("message", "Session destroyed");
        return ResponseEntity.ok().body(JSON);
    }

    // Destroy session
//    @GetMapping("/destroy")
//    public ResponseEntity<?> destroySession() {
//        return
//    }
}