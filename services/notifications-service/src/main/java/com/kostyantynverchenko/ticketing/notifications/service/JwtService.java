package com.kostyantynverchenko.ticketing.notifications.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
public class JwtService {

    private final SecretKey signingKey;

    public JwtService(@Value("${jwt.secret}") String secretKey) {
        this.signingKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public UUID extractUserId(String token) {
        Claims claims = parseJwt(token);
        return UUID.fromString(claims.getSubject());
    }

    public Claims parseJwt(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
