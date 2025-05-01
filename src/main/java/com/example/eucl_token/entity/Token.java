package com.example.eucl_token.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "purchased_tokens")
@Data
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 6)
    private String meterNumber;

    @Column(nullable = false, length = 16)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TokenStatus tokenStatus;

    @Column(nullable = false)
    private Integer tokenValueDays;

    @Column(nullable = false)
    private LocalDateTime purchasedDate;

    @Column(nullable = false)
    private Long amount;

    public enum TokenStatus {
        USED, NEW, EXPIRED
    }
}