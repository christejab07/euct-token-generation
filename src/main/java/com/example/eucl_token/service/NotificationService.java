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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    private final TokenRepository tokenRepository;
    private final MeterRepository meterRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    public void checkExpiringTokens() {
        logger.debug("Checking for expiring tokens and updating token statuses");
        List<Token> tokens = tokenRepository.findAll();
        LocalDateTime now = LocalDateTime.now();

        for (Token token : tokens) {
            // Update token statuses
            long daysSincePurchase = Duration.between(token.getPurchasedDate(), now).toDays();
            boolean isExpired = now.isAfter(token.getExpiryDate());

            if (isExpired && token.getTokenStatus() != Token.TokenStatus.EXPIRED) {
                token.setTokenStatus(Token.TokenStatus.EXPIRED);
                tokenRepository.save(token);
                logger.info("Token ID: {} status updated to EXPIRED", token.getId());
            } else if (daysSincePurchase >= 1 && token.getTokenStatus() == Token.TokenStatus.NEW) {
                token.setTokenStatus(Token.TokenStatus.USED);
                tokenRepository.save(token);
                logger.info("Token ID: {} status updated to USED", token.getId());
            }

            // Check for expiring tokens
            long hoursUntilExpiry = Duration.between(now, token.getExpiryDate()).toHours();
            // Notify if 4.5 to 5.5 hours remain to account for manual triggering
            if (hoursUntilExpiry >= 4 && hoursUntilExpiry <= 5 && token.getTokenStatus() != Token.TokenStatus.EXPIRED) {
                String meterNumber = token.getMeterNumber();
                Meter meter = meterRepository.findByMeterNumber(meterNumber).get(0);
                User user = userRepository.findById(meter.getUser().getId())
                        .orElseThrow(() -> {
                            logger.warn("User not found for user ID: {}", meter.getUser().getId());
                            return new CustomException("User not found");
                        });
                String message = String.format(
                        "Dear %s, REG is pleased to remind you that the token for meter %s is going to expire in less than 5 hours. Please purchase a new token.",
                        user.getName(), meter.getMeterNumber());
                Notification notification = new Notification();
                notification.setMeterNumber(meter.getMeterNumber());
                notification.setMessage(message);
                notification.setIssuedDate(LocalDateTime.now());
                Notification savedNotification = notificationRepository.save(notification);

                NotificationDTO notificationDTO = new NotificationDTO();
                notificationDTO.setId(savedNotification.getId());
                notificationDTO.setMeterNumber(savedNotification.getMeterNumber());
                notificationDTO.setMessage(savedNotification.getMessage());
                notificationDTO.setIssuedDate(savedNotification.getIssuedDate());
                logger.info("Notification created for token ID: {} with 5 hours until expiry", token.getId());
                // Email notification logic to be implemented later
            }
        }
    }
}