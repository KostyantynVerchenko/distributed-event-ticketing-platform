package com.kostyantynverchenko.ticketing.auth.service;

import com.kostyantynverchenko.ticketing.auth.dto.AuthResponse;
import com.kostyantynverchenko.ticketing.auth.dto.LoginRequest;
import com.kostyantynverchenko.ticketing.auth.dto.RegisterRequest;
import com.kostyantynverchenko.ticketing.auth.entity.User;
import com.kostyantynverchenko.ticketing.auth.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private UserRepository userRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public AuthResponse registerUser(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already in use: " + request.getEmail());
        }

        User user = new User();

        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getRawPassword()));

        user = userRepository.save(user);

        String token = jwtService.generateJwtToken(user);

        return new AuthResponse(token, user.getId(), user.getEmail());
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getRawPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtService.generateJwtToken(user);

        return new AuthResponse(token, user.getId(), user.getEmail());
    }
}
