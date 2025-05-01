package com.example.eucl_token.service;

import com.example.eucl_token.dto.NotificationDTO;
import com.example.eucl_token.entity.Meter;
import com.example.eucl_token.entity.Notification;
import com.example.eucl_token.entity.Token;
import com.example.eucl_token.entity.User;
import com.example.eucl_token.exception.CustomException;
import com.example.eucl_token.repository.MeterRepository;
import com.example.eucl_token.repository.NotificationRepository;
import com.example.eucl_token.repository.TokenRepository;
import com.example.eucl_token.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final MeterRepository meterRepository;

    public void checkExpiringTokens() {
        List<Token> tokens = tokenRepository.findAll();
        LocalDateTime now = LocalDateTime.now();
        for (Token token : tokens) {
            LocalDateTime expiryDate = token.getPurchasedDate().plusDays(token.getTokenValueDays());
            long hoursUntilExpiry = java.time.Duration.between(now, expiryDate).toHours();
            if (hoursUntilExpiry == 5 && token.getTokenStatus() == Token.TokenStatus.NEW) {
                Meter meter = meterRepository.findByMeterNumber(token.getMeterNumber())
                        .stream()
                        .findFirst()
                        .orElseThrow(() -> new CustomException("Meter not found"));
                User user = userRepository.findById(meter.getUser().getId())
                        .orElseThrow(() -> new CustomException("User not found"));
                String message = String.format(
                        "Dear %s, REG is pleased to remind you that the token in the %s is going to expire in 5 hours. Please purchase a new token.",
                        user.getName(), token.getMeterNumber());
                Notification notification = new Notification();
                notification.setMeterNumber(token.getMeterNumber());
                notification.setMessage(message);
                notification.setIssuedDate(LocalDateTime.now());
                Notification savedNotification = notificationRepository.save(notification);

                NotificationDTO notificationDTO = new NotificationDTO();
                notificationDTO.setId(savedNotification.getId());
                notificationDTO.setMeterNumber(savedNotification.getMeterNumber());
                notificationDTO.setMessage(savedNotification.getMessage());
                notificationDTO.setIssuedDate(savedNotification.getIssuedDate());
                // Email notification logic to be implemented later
            }
        }
    }
}