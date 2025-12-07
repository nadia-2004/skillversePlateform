package com.example.notification_service.controller;

import com.example.notification_service.entity.NotificationHistory;
import com.example.notification_service.service.NotificationHistoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/history")
public class NotificationHistoryController {

    private final NotificationHistoryService service;

    public NotificationHistoryController(NotificationHistoryService service) {
        this.service = service;
    }

    @GetMapping
    public List<NotificationHistory> getAll() {
        return service.findAll();
    }

    @PostMapping
    public NotificationHistory create(@RequestBody NotificationHistory history) {
        return service.save(history);
    }
}
