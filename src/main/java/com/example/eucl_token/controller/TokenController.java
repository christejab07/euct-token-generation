package com.example.eucl_token.controller;

import com.example.eucl_token.dto.TokenDTO;
import com.example.eucl_token.entity.Token;
import com.example.eucl_token.repository.TokenRepository;
import com.example.eucl_token.service.TokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tokens")
@RequiredArgsConstructor
public class TokenController {
    private final TokenService tokenService;
    private final TokenRepository tokenRepository;
    @PostMapping("/purchase")
    public ResponseEntity<TokenDTO> purchaseToken(@Valid @RequestBody TokenDTO tokenDTO) {
        return ResponseEntity.ok(tokenService.purchaseToken(tokenDTO));
    }

    @GetMapping("/validate")
    public ResponseEntity<TokenDTO> validateToken(@RequestParam String token) {
        return ResponseEntity.ok(tokenService.validateToken(token));
    }

    @GetMapping("/meter/{meterNumber}")
    public ResponseEntity<List<TokenDTO>> getTokensByMeterNumber(@PathVariable String meterNumber) {
        List<Token> tokens = tokenRepository.findByMeterNumber(meterNumber);
        List<TokenDTO> tokenDTOs = tokens.stream().map(token -> {
            return getTokenDTO(token);
        }).collect(Collectors.toList());
        return ResponseEntity.ok(tokenDTOs);
    }

    public static TokenDTO getTokenDTO(Token token) {
        TokenDTO dto = new TokenDTO();
        dto.setId(token.getId());
        dto.setMeterNumber(token.getMeterNumber());
        dto.setToken(token.getToken());
        dto.setTokenStatus(token.getTokenStatus().name());
        dto.setTokenValueDays(token.getTokenValueDays());
        dto.setPurchasedDate(token.getPurchasedDate());
        dto.setAmount(token.getAmount());
        return dto;
    }
}