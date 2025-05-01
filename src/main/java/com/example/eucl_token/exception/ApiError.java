package com.example.eucl_token.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Data
@AllArgsConstructor
public class ApiError {
    private HttpStatus status;
    private String message;
    private Map<String, String> errors;
}
