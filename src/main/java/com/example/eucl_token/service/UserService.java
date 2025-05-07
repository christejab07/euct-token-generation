package com.example.eucl_token.service;

import com.example.eucl_token.dto.UserDTO;
import com.example.eucl_token.entity.User;
import com.example.eucl_token.exception.CustomException;
import com.example.eucl_token.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserDTO registerUser(UserDTO userDTO) {
        logger.debug("Registering user with email: {}", userDTO.getEmail());
        if (userRepository.existsByEmailOrPhoneOrNationalId(userDTO.getEmail(), userDTO.getPhone(), userDTO.getNationalId())) {
            logger.warn("Registration failed: Email, phone, or national ID already exists for email: {}", userDTO.getEmail());
            throw new CustomException("Email, phone, or national ID already exists");
        }
        User user = new User();
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setPhone(userDTO.getPhone());
        user.setNationalId(userDTO.getNationalId());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setRole(User.Role.valueOf(userDTO.getRole()));
        User savedUser = userRepository.save(user);

        UserDTO responseDTO = new UserDTO();
        responseDTO.setName(savedUser.getName());
        responseDTO.setEmail(savedUser.getEmail());
        responseDTO.setPhone(savedUser.getPhone());
        responseDTO.setNationalId(savedUser.getNationalId());
        responseDTO.setRole(savedUser.getRole().name());
        logger.info("User registered successfully with email: {}", savedUser.getEmail());
        return responseDTO;
    }

    public UserDTO loginUser(String email, String password) {
        logger.debug("Attempting login for email: {}", email);
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            logger.warn("Login failed: Email or password is empty");
            throw new CustomException("Email and password are required");
        }
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("Login failed: User not found for email: {}", email);
                    return new CustomException("Invalid email or password");
                });
        if (!passwordEncoder.matches(password, user.getPassword())) {
            logger.warn("Login failed: Incorrect password for email: {}", email);
            throw new CustomException("Invalid email or password");
        }

        UserDTO responseDTO = new UserDTO();
        responseDTO.setName(user.getName());
        responseDTO.setEmail(user.getEmail());
        responseDTO.setPhone(user.getPhone());
        responseDTO.setNationalId(user.getNationalId());
        responseDTO.setRole(user.getRole().name());
        logger.info("User logged in successfully with email: {}", email);
        return responseDTO;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> {
                    logger.warn("User not found with email: {}", username);
                    return new UsernameNotFoundException("User not found with email: " + username);
                });
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name()))
        );
    }
}