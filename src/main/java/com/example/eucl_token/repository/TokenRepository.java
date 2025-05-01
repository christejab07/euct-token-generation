package com.example.eucl_token.repository;

import com.example.eucl_token.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TokenRepository extends JpaRepository<Token, Long> {
    List<Token> findByMeterNumber(String meterNumber);
    boolean existsByToken(String token);
}
