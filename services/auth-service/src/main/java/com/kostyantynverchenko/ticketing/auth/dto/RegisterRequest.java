package com.kostyantynverchenko.ticketing.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

    @Email
    @NotBlank
    private String email;

    @Size(min = 8, message = "Password must be at least 8 characters long")
    @NotBlank
    private String rawPassword;

    public RegisterRequest() {}

    public RegisterRequest(String email, String rawPassword) {
        this.email = email;
        this.rawPassword = rawPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRawPassword() {
        return rawPassword;
    }

    public void setRawPassword(String rawPassword) {
        this.rawPassword = rawPassword;
    }
}
