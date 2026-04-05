package com.tradesphere.identity.security;

import com.tradesphere.identity.domain.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.access-token-expiry-ms}")
    private long expirationWindow;

    public String generateToken(User user) {
        return Jwts.builder()
                .claim("userId", user.getUserId().toString())
                .claim("role", user.getRole())
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .expiration(new Date(System.currentTimeMillis() + expirationWindow))
                .compact();

    }
}
