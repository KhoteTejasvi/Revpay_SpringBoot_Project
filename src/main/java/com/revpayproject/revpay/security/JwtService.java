package com.revpayproject.revpay.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    private SecretKey key;

    private final String SECRET =
            "mySuperSecretKeymySuperSecretKeymySuperSecretKey";
    // must be 32+ characters

    @PostConstruct
    public void init() {
        key = Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    // 🔹 Generate Token
    public String generateToken(String email) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hour
                .signWith(key)
                .compact();
    }

    // 🔹 Extract Email
    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    // 🔹 Extract Claims
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}