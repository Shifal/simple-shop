package com.simple_shop.util;

import com.simple_shop.constants.ResponseMessages;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private String customerId;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        customerId = "CUST123";
    }

    @Test
    void testGenerateToken_NotNull() {
        String token = jwtUtil.generateToken(customerId);
        assertNotNull(token, "Token should not be null");
    }

    @Test
    void testExtractCustomerId_ReturnsCorrectId() {
        String token = jwtUtil.generateToken(customerId);
        String extractedId = jwtUtil.extractCustomerId(token);
        assertEquals(customerId, extractedId, "Extracted customerId should match");
    }

    @Test
    void testIsTokenExpired_ReturnsFalseForFreshToken() {
        String token = jwtUtil.generateToken(customerId);
        assertFalse(jwtUtil.isTokenExpired(token), "Fresh token should not be expired");
    }

    @Test
    void testValidateToken_ReturnsTrueForValidToken() {
        String token = jwtUtil.generateToken(customerId);
        boolean result = jwtUtil.validateToken(token, customerId);
        assertTrue(result, "Valid token should return true");
    }

    @Test
    void testValidateToken_ThrowsException_ForWrongCustomerId() {
        String token = jwtUtil.generateToken(customerId);
        JwtException exception = assertThrows(JwtException.class, () -> {
            jwtUtil.validateToken(token, "WRONG_ID");
        });

        assertEquals("Invalid or corrupted token.", exception.getMessage());
    }


    @Test
    void testValidateToken_ThrowsException_ForExpiredToken() throws InterruptedException {
        // Create a JwtUtil instance with 1-second expiry for testing
        JwtUtil shortLivedJwtUtil = new JwtUtil() {
            @Override
            public String generateToken(String customerId) {
                return io.jsonwebtoken.Jwts.builder()
                        .setSubject(customerId)
                        .setIssuedAt(new java.util.Date())
                        .setExpiration(new java.util.Date(System.currentTimeMillis() + 1000)) // 1 second
                        .signWith(io.jsonwebtoken.security.Keys.hmacShaKeyFor("ShifalSecretKey123ShifalSecretKey123".getBytes()))
                        .compact();
            }
        };

        String token = shortLivedJwtUtil.generateToken(customerId);

        // Wait for token to expire
        TimeUnit.SECONDS.sleep(2);

        JwtException exception = assertThrows(JwtException.class, () -> {
            shortLivedJwtUtil.validateToken(token, customerId);
        });

        assertEquals(ResponseMessages.TOKEN_EXPIRED, exception.getMessage());
    }

    @Test
    void testValidateToken_ThrowsException_ForMalformedToken() {
        String invalidToken = "this.is.not.a.valid.token";

        JwtException exception = assertThrows(JwtException.class, () -> {
            jwtUtil.validateToken(invalidToken, customerId);
        });

        assertEquals("Invalid JWT signature or malformed token.", exception.getMessage());
    }
}
