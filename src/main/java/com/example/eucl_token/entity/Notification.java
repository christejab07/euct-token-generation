package com.example.eucl_token.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 6)
    private String meterNumber;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private LocalDateTime issuedDate;
}