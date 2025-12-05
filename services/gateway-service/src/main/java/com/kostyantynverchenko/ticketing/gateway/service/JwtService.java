package com.kostyantynverchenko.ticketing.gateway.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
@Slf4j
public class JwtService {

    private final SecretKey signingKey;
    private final long expirationTime;

    public JwtService(@Value("${jwt.secret}") String secret, @Value("${jwt.expiration-ms}") long expirationTime) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationTime = expirationTime;
    }

    public String extractEmail(String token) {
        Claims claims = parseJwtToken(token);
        return claims.get("email").toString();
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = parseJwtToken(token);
            Date exp = claims.getExpiration();
            return exp.after(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("JWT Token is invalid: {}", e.getMessage());
            return false;
        }
    }

    public Claims parseJwtToken(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}