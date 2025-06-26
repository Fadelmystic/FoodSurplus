package com.surplusfood.ordermanagement.order_service.application.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class JwtService {
    private final String secretKey = "your-secret-key"; // Load from config

    public String extractUserId(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject(); // Or claims.get("user_id") depending on how it's set
    }
}
