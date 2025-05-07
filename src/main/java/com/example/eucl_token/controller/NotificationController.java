package com.example.eucl_token.controller;

import com.example.eucl_token.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @PostMapping("/check-expiring")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> checkExpiringTokens() {
        notificationService.checkExpiringTokens();
        return ResponseEntity.ok().build();
    }
}
