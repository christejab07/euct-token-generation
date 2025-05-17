package com.example.eucl_token.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TokenDTO {
    private Long id;

    @NotBlank(message = "Meter number is required")
    @Pattern(regexp = "^[0-9]{6}$", message = "Meter number must be 6 numeric characters")
    private String meterNumber;

    private String token;

    private String tokenStatus;

    private Integer tokenValueDays;

    private LocalDateTime purchasedDate;

    private LocalDateTime expiryDate;

    private Long amount;
}