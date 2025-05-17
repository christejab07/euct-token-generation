package com.example.eucl_token.service;

import com.example.eucl_token.dto.TokenDTO;
import com.example.eucl_token.entity.Token;
import com.example.eucl_token.exception.CustomException;
import com.example.eucl_token.repository.MeterRepository;
import com.example.eucl_token.repository.TokenRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class TokenService {
    private static final Logger logger = LoggerFactory.getLogger(TokenService.class);
    @Getter
    private final TokenRepository tokenRepository;
    private final MeterRepository meterRepository;

    public TokenDTO purchaseToken(TokenDTO tokenDTO) {
        if (!meterRepository.existsByMeterNumber(tokenDTO.getMeterNumber())) {
            logger.warn("Purchase failed: Meter number {} not found", tokenDTO.getMeterNumber());
            throw new CustomException("Invalid meter number");
        }
        Long amount = tokenDTO.getAmount();
        if (amount < 100 || amount % 100 != 0) {
            throw new CustomException("Amount must be a multiple of 100 and at least 100 RWF");
        }
        int days = (int) (amount / 100);
        if (days > 365 * 5) {
            throw new CustomException("Token duration cannot exceed 5 years");
        }
        String tokenValue = generateUniqueToken();
        Token token = new Token();
        token.setMeterNumber(tokenDTO.getMeterNumber());
        token.setToken(tokenValue);
        token.setTokenStatus(Token.TokenStatus.NEW);
        token.setTokenValueDays(days);
        token.setPurchasedDate(LocalDateTime.now());
        token.setExpiryDate(LocalDateTime.now().plusDays(days));
        token.setAmount(amount);
        Token savedToken = tokenRepository.save(token);

        return getTokenDTO(savedToken);
    }

    public TokenDTO validateToken(String token) {
        Token foundToken = tokenRepository.findAll().stream()
                .filter(t -> t.getToken().equals(token))
                .findFirst()
                .orElseThrow(() -> new CustomException("Token not found"));

        return getTokenDTO(foundToken);
    }

    private TokenDTO getTokenDTO(Token foundToken) {
        TokenDTO responseDTO = new TokenDTO();
        responseDTO.setId(foundToken.getId());
        responseDTO.setMeterNumber(foundToken.getMeterNumber());
        responseDTO.setAmount(foundToken.getAmount());
        responseDTO.setToken(foundToken.getToken());
        responseDTO.setTokenStatus(foundToken.getTokenStatus().name());
        responseDTO.setTokenValueDays(foundToken.getTokenValueDays());
        responseDTO.setPurchasedDate(foundToken.getPurchasedDate());
        responseDTO.setExpiryDate(foundToken.getExpiryDate());
        return responseDTO;
    }

    private String generateUniqueToken() {
        Random random = new Random();
        String token;
        do {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 16; i++) {
                sb.append(random.nextInt(10));
            }
            token = sb.toString();
        } while (tokenRepository.existsByToken(token));
        return token;
    }

}