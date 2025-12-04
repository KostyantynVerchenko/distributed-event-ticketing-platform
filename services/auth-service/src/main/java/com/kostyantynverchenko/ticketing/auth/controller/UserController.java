package com.kostyantynverchenko.ticketing.auth.controller;

import com.kostyantynverchenko.ticketing.auth.dto.AuthResponse;
import com.kostyantynverchenko.ticketing.auth.dto.LoginRequest;
import com.kostyantynverchenko.ticketing.auth.dto.RegisterRequest;
import com.kostyantynverchenko.ticketing.auth.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@Slf4j
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/auth/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        AuthResponse response = userService.registerUser(request);

        return ok(response);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        AuthResponse response = userService.login(request);

        return ok(response);
    }
}
