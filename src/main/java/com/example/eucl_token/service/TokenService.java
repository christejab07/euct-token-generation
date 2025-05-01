package com.example.eucl_token.service;

import com.example.eucl_token.dto.TokenDTO;
import com.example.eucl_token.entity.Token;
import com.example.eucl_token.exception.CustomException;
import com.example.eucl_token.repository.MeterRepository;
import com.example.eucl_token.repository.TokenRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class TokenService {
    @Getter
    private final TokenRepository tokenRepository;
    private final MeterRepository meterRepository;

    public TokenDTO purchaseToken(TokenDTO tokenDTO) {
        if (!meterRepository.existsByMeterNumber(tokenDTO.getMeterNumber())) {
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
        return getTokenDTO(foundToken);
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