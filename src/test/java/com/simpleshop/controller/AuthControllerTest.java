package com.simpleshop.controller;

import com.simpleshop.response.ApiResponse;
import com.simpleshop.service.KeycloakAuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class AuthControllerTest {

    private KeycloakAuthService keycloakAuthService;
    private AuthController authController;

    @BeforeEach
    void setUp() {
        keycloakAuthService = Mockito.mock(KeycloakAuthService.class);
        authController = new AuthController(keycloakAuthService);
    }


    @Test
    void login_ShouldReturn400_WhenEmailOrPasswordMissing() {
        Map<String, String> payload = Map.of("email", "user@mail.com");

        ResponseEntity<ApiResponse> response = authController.login(payload);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("email and password are required", response.getBody().getMessage());
    }

    @Test
    void login_ShouldReturn200_WhenValidCredentials() {
        Map<String, Object> tokenResponse = Map.of("access_token", "abc123");

        when(keycloakAuthService.loginWithPassword("user@mail.com", "pass123")).thenReturn(tokenResponse);

        Map<String, String> payload = Map.of(
                "email", "user@mail.com",
                "password", "pass123"
        );

        ResponseEntity<ApiResponse> response = authController.login(payload);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Login successful", response.getBody().getMessage());
        assertEquals(tokenResponse, response.getBody().getData());
    }

    @Test
    void login_ShouldReturn401_WhenInvalidCredentials() {
        when(keycloakAuthService.loginWithPassword(anyString(), anyString())).thenReturn(null);

        Map<String, String> payload = Map.of(
                "email", "user@mail.com",
                "password", "wrong"
        );

        ResponseEntity<ApiResponse> response = authController.login(payload);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Invalid credentials or Keycloak error", response.getBody().getMessage());
    }


    @Test
    void verify_ShouldReturn400_WhenAuthorizationHeaderMissing() {
        ResponseEntity<ApiResponse> response = authController.verify(null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Missing or invalid Authorization header", response.getBody().getMessage());
    }

    @Test
    void verify_ShouldReturn401_WhenTokenNotActive() {
        Map<String, Object> inactiveToken = Map.of("active", false);

        when(keycloakAuthService.introspectToken("token123")).thenReturn(inactiveToken);

        ResponseEntity<ApiResponse> response = authController.verify("Bearer token123");

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Token is not active", response.getBody().getMessage());
    }

    @Test
    void verify_ShouldReturn200_WhenTokenActiveAndValid() {
        Map<String, Object> activeToken = Map.of(
                "active", true,
                "name", "John Doe",
                "preferred_username", "jdoe",
                "email", "john@mail.com",
                "realm_access", Map.of("roles", java.util.List.of("USER"))
        );

        when(keycloakAuthService.introspectToken("validToken")).thenReturn(activeToken);

        ResponseEntity<ApiResponse> response = authController.verify("Bearer validToken");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Token is valid", response.getBody().getMessage());

        Map<String, Object> body = (Map<String, Object>) response.getBody().getData();
        assertEquals("USER", body.get("role"));
    }

    @Test
    void verify_ShouldReturn200_WhenTokenRoleIsAdmin() {
        Map<String, Object> activeAdminToken = Map.of(
                "active", true,
                "realm_access", Map.of("roles", java.util.List.of("ADMIN"))
        );

        when(keycloakAuthService.introspectToken("adminToken")).thenReturn(activeAdminToken);

        ResponseEntity<ApiResponse> response = authController.verify("Bearer adminToken");

        assertEquals(HttpStatus.OK, response.getStatusCode());

        Map<String, Object> body = (Map<String, Object>) response.getBody().getData();
        assertEquals("ADMIN", body.get("role"));
    }


    @Test
    void logout_ShouldReturn400_WhenTokenIsMissing() {
        ResponseEntity<ApiResponse> response = authController.logout(Map.of());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("refresh token missing in body", response.getBody().getMessage());
    }

    @Test
    void logout_ShouldReturn200_WhenLogoutSuccess() {
        when(keycloakAuthService.logoutByRefreshToken("refToken")).thenReturn(true);

        ResponseEntity<ApiResponse> response = authController.logout(Map.of("token", "refToken"));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Logout successful", response.getBody().getMessage());
    }

    @Test
    void logout_ShouldReturn400_WhenInvalidRefreshToken() {
        when(keycloakAuthService.logoutByRefreshToken("badToken")).thenReturn(false);

        ResponseEntity<ApiResponse> response = authController.logout(Map.of("token", "badToken"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid or expired refresh token. Logout failed.", response.getBody().getMessage());
    }
}
