package com.exchange.controller;

import com.exchange.model.dto.CreateSessionRequest;
import com.exchange.model.dto.SessionResponse;
import com.exchange.service.SessionManagementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/sessions")
@Slf4j
public class SessionController {

    private final SessionManagementService sessionService;

    public SessionController(SessionManagementService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping
    public ResponseEntity<SessionResponse> createSession(@RequestBody CreateSessionRequest request) {
        log.info("Creating session with request: {}", request);
        
        // Simplifié pour le test
        SessionResponse response = SessionResponse.builder()
                .sessionId("test-" + System.currentTimeMillis())
                .title(request.getTitle())
                .hostUserId(request.getHostUserId())
                .status("CREATED")
                .build();
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{sessionId}")
    public ResponseEntity<Map<String, String>> getSession(@PathVariable String sessionId) {
        Map<String, String> response = new HashMap<>();
        response.put("sessionId", sessionId);
        response.put("status", "ACTIVE");
        response.put("message", "Test session");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Exchange Service is running!");
    }
}