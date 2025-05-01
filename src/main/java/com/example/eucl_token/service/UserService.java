package com.example.eucl_token.service;

import com.example.eucl_token.dto.UserDTO;
import com.example.eucl_token.entity.User;
import com.example.eucl_token.exception.CustomException;
import com.example.eucl_token.repository.UserRepository;
import lombok.RequiredArgsConstructor;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
//    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

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
        user.setPassword(userDTO.getPassword());
        user.setRole(User.Role.valueOf(userDTO.getRole()));
        User savedUser = userRepository.save(user);

        logger.info("User registered successfully with email: {}", savedUser.getEmail());
        // Map to DTO to exclude password
        return getUserDTO(savedUser);
    }

    private UserDTO getUserDTO(User savedUser) {
        UserDTO responseDTO = new UserDTO();
        responseDTO.setName(savedUser.getName());
        responseDTO.setEmail(savedUser.getEmail());
        responseDTO.setPhone(savedUser.getPhone());
        responseDTO.setNationalId(savedUser.getNationalId());
        responseDTO.setRole(savedUser.getRole().name());
        return responseDTO;
    }
    public UserDTO loginUser(String email, String password) {
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            throw new CustomException("Email and password are required");
        }
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("Invalid email. This email isn't in the database."));
        if (!user.getPassword().equals(password)) {
            logger.warn("Login failed: Incorrect password for email: {}", email);
            throw new CustomException("Uncorresponding credentials. Invalid password");
        }

        logger.info("User logged in successfully with email: {}", email);
        // Map to DTO to exclude password
        return getUserDTO(user);
    }
}