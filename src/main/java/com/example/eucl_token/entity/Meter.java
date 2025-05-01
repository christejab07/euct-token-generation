package com.example.eucl_token.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "meters")
@Data
public class Meter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 6)
    private String meterNumber;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}