package com.example.eucl_token.service;

import com.example.eucl_token.dto.MeterDTO;
import com.example.eucl_token.entity.Meter;
import com.example.eucl_token.entity.User;
import com.example.eucl_token.exception.CustomException;
import com.example.eucl_token.repository.MeterRepository;
import com.example.eucl_token.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MeterService {
    private static final Logger logger = LoggerFactory.getLogger(MeterService.class);
    private final MeterRepository meterRepository;
    private final UserRepository userRepository;

    public MeterDTO registerMeter(MeterDTO meterDTO) {
        if (meterRepository.existsByMeterNumber(meterDTO.getMeterNumber())) {
            logger.warn("Registration failed: Meter number {} already exists", meterDTO.getMeterNumber());
            throw new CustomException("Meter number already exists");
        }
        User user = userRepository.findById(meterDTO.getUserId())
                .orElseThrow(() -> {
                    logger.warn(("user with id {} not found"), meterDTO.getUserId());
                    return new CustomException("User not found");
                });
        Meter meter = new Meter();
        meter.setMeterNumber(meterDTO.getMeterNumber());
        meter.setUser(user);
        Meter savedMeter = meterRepository.save(meter);

        MeterDTO responseDTO = new MeterDTO();
        responseDTO.setId(savedMeter.getId());
        responseDTO.setMeterNumber(savedMeter.getMeterNumber());
        responseDTO.setUserId(savedMeter.getUser().getId());
        logger.info("Meter registered successfully with number: {}", savedMeter.getMeterNumber());
        return responseDTO;
    }
}