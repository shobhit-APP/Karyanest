package com.backend.karyanestApplication.JwtSecurity;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {
    private String SECRET_KEY;

    @Value("${jwt.expiration}") // JWT expiration time from properties
    private long JWT_EXPIRATION;

    public JwtUtil() {
        try {
            // Generate secret key for JWT signing
            KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA256");
            SecretKey sk = keyGenerator.generateKey();
            SECRET_KEY = Base64.getEncoder().encodeToString(sk.getEncoded());
            System.out.println("Secret key generated successfully.");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    // Generate JWT Token with username and role
    public String generateToken(String username, String role, Long userId,String fullname) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role); // Add role to claims
        claims.put("id", userId); // Add userId to claims
        claims.put("fullName",fullname);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Validate JWT Token with username and role
    public boolean validateToken(String token, String username, String role) {
        try {
            Claims claims = getClaims(token);
            String extractedUsername = claims.getSubject();
            String extractedRole = claims.get("role", String.class);
            Date expirationDate = claims.getExpiration();

            return extractedUsername.equals(username) &&
                    extractedRole.equals(role) &&
                    expirationDate.after(new Date());
        } catch (ExpiredJwtException e) {
            System.out.println("JWT Token expired: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.out.println("Invalid JWT Token: " + e.getMessage());
            return false;
        }
    }

    // Extract username from JWT token
    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    // Extract role from JWT token
    public String extractRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    // Extract value from JWT token
    public <T> T extractJWTValue(String token, String key,Class<T> type) {
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("Key cannot be null or empty");
        }
        return getClaims(token).get(key, type);
    }

    // Extract claims from JWT token
    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String fullname(String token) {
        return getClaims(token).get("fullName", String.class);

    }
}
