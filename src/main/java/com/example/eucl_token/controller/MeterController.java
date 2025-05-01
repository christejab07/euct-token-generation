package com.example.eucl_token.controller;

import com.example.eucl_token.dto.MeterDTO;
import com.example.eucl_token.entity.Meter;
import com.example.eucl_token.service.MeterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/meters")
@RequiredArgsConstructor
public class MeterController {
    private final MeterService meterService;

    @PostMapping("/register")
    public ResponseEntity<MeterDTO> registerMeter(@Valid @RequestBody MeterDTO meterDTO) {
        return ResponseEntity.ok(meterService.registerMeter(meterDTO));
    }
}