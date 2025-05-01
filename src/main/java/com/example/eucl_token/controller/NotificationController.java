package com.example.eucl_token.controller;

import com.example.eucl_token.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @PostMapping("/check-expiring")
    public ResponseEntity<Void> checkExpiringTokens() {
        notificationService.checkExpiringTokens();
        return ResponseEntity.ok().build();
    }
}
