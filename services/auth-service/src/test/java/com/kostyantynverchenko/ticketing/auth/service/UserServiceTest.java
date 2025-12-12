package com.kostyantynverchenko.ticketing.auth.service;

import com.kostyantynverchenko.ticketing.auth.dto.AuthResponse;
import com.kostyantynverchenko.ticketing.auth.dto.LoginRequest;
import com.kostyantynverchenko.ticketing.auth.dto.RegisterRequest;
import com.kostyantynverchenko.ticketing.auth.entity.User;
import com.kostyantynverchenko.ticketing.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserService userService;

    private final UUID userId = UUID.randomUUID();

    @Test
    void registerUserShouldPersistNewUserWithEncodedPassword() {
        when(jwtService.generateJwtToken(any(User.class))).thenReturn("token");
        when(passwordEncoder.encode(any())).thenReturn("encoded-password");

        RegisterRequest request = new RegisterRequest("test@example.com", "plain-password");
        User user = new User();

        user.setId(userId);
        user.setEmail(request.getEmail());
        user.setPasswordHash("encoded-password");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        AuthResponse response = userService.registerUser(request);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(captor.capture());

        assertThat(captor.getValue().getEmail()).isEqualTo(request.getEmail());
        assertThat(captor.getValue().getPasswordHash()).isEqualTo("encoded-password");
        assertThat(response.getUserId()).isEqualTo(userId);
        assertThat(response.getEmail()).isEqualTo(request.getEmail());
        assertThat(response.getToken()).isEqualTo("token");
    }

    @Test
    void loginShouldThrowWhenPasswordDoesNotMatch() {
        LoginRequest request = new LoginRequest("test@example.com", "wrong-password");
        User existingUser = new User();

        existingUser.setId(userId);
        existingUser.setEmail(request.getEmail());
        existingUser.setPasswordHash("encoded-password");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches(request.getRawPassword(), existingUser.getPasswordHash())).thenReturn(false);

        assertThrows(RuntimeException.class, () -> userService.login(request));
    }

    @Test
    void loginShouldReturnTokenWhenCredentialsValid() {
        when(jwtService.generateJwtToken(any(User.class))).thenReturn("token");

        LoginRequest request = new LoginRequest("test@example.com", "plain-password");
        User existingUser = new User();

        existingUser.setId(userId);
        existingUser.setEmail(request.getEmail());
        existingUser.setPasswordHash("encoded-password");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches(request.getRawPassword(), existingUser.getPasswordHash())).thenReturn(true);

        AuthResponse response = userService.login(request);

        verify(jwtService).generateJwtToken(existingUser);
        assertThat(response.getUserId()).isEqualTo(userId);
        assertThat(response.getEmail()).isEqualTo(request.getEmail());
        assertThat(response.getToken()).isEqualTo("token");
    }

}