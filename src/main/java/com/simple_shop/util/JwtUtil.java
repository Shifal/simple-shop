package com.simple_shop.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    private static final String SECRET_KEY = "ShifalSecretKey123ShifalSecretKey123";
    private static final Key KEY = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    public String generateToken(Long customerId) {
        return Jwts.builder()
                .setSubject(String.valueOf(customerId))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hour
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public Long extractCustomerId(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return Long.parseLong(claims.getSubject());
    }

    public boolean isTokenExpired(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getExpiration().before(new Date());
    }

    public boolean validateToken(String token, Long customerId) {
        Long extractedId = extractCustomerId(token);
        return (extractedId.equals(customerId) && !isTokenExpired(token));
    }
}
