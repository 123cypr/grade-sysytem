package com.example.grade.security;

import com.example.grade.model.AuthPayload;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey key;
    private final long expirationMs;

    public JwtUtil(@Value("${security.jwt.secret:dev-secret-key-please-change}") String secret,
                   @Value("${security.jwt.expire-ms:86400000}") long expirationMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.expirationMs = expirationMs;
    }

    public String generate(AuthPayload payload) {
        return Jwts.builder()
                .setSubject(payload.getUsername())
                .claim("uid", payload.getUserId())
                .claim("role", payload.getRoleType())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public AuthPayload parse(String token) {
        Claims body = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        AuthPayload payload = new AuthPayload();
        payload.setUsername(body.getSubject());
        payload.setUserId(body.get("uid", Long.class));
        payload.setRoleType(body.get("role", Integer.class));
        return payload;
    }
}
