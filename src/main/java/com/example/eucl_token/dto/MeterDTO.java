package com.example.eucl_token.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class MeterDTO {
    private Long id;

    @NotBlank(message = "Meter number is required")
    @Pattern(regexp = "^[0-9]{6}$", message = "Meter number must be 6 numeric characters")
    private String meterNumber;

    private Long userId;
}