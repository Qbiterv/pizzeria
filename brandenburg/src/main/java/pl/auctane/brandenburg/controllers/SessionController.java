package pl.auctane.brandenburg.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebExchangeDecorator;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/session")
public class SessionController {

    // Check if there's an active session
    @GetMapping("/check")
    public ResponseEntity<?> checkSession() {
        return ResponseEntity.ok().body("Session check");
    }

    // Create a session
//    @GetMapping("/create")
//    public ResponseEntity<?> createSession() {
//        return
//    }

    // Destroy session
//    @GetMapping("/destroy")
//    public ResponseEntity<?> destroySession() {
//        return
//    }
}