package com.example.eucl_token.repository;

import com.example.eucl_token.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmailOrPhoneOrNationalId(String email, String phone, String nationalId);
}
