package com.example.eucl_token.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationDTO {
    private Long id;

    @NotBlank(message = "Meter number is required")
    @Pattern(regexp = "^[0-9]{6}$", message = "Meter number must be 6 numeric characters")
    private String meterNumber;

    @NotBlank(message = "Message is required")
    private String message;

    private LocalDateTime issuedDate;
}
