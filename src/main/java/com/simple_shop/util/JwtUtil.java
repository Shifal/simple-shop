package com.simple_shop.util;

import com.simple_shop.constants.ResponseMessages;
import io.jsonwebtoken.JwtException;
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
        try {
            Long extractedId = extractCustomerId(token);

            if (!extractedId.equals(customerId)) {
                throw new JwtException("Token does not belong to this user.");
            }

            if (isTokenExpired(token)) {
                throw new JwtException(ResponseMessages.TOKEN_EXPIRED);
            }

            return true;

        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            throw new JwtException(ResponseMessages.TOKEN_EXPIRED);
        } catch (io.jsonwebtoken.SignatureException | io.jsonwebtoken.MalformedJwtException e) {
            throw new JwtException("Invalid JWT signature or malformed token.");
        } catch (Exception e) {
            throw new JwtException("Invalid or corrupted token.");
        }
    }


}
