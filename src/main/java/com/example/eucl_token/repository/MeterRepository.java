package com.example.eucl_token.repository;

import com.example.eucl_token.entity.Meter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MeterRepository extends JpaRepository<Meter,Long> {
    boolean existsByMeterNumber(String meterNumber);
    List<Meter> findByMeterNumber(String meterNumber);
}
