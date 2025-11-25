package com.simpleshop.controller;

import com.simpleshop.response.ApiResponse;
import com.simpleshop.service.KeycloakAuthService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Map;

@RestController
@RequestMapping("${api.auth.base}")
public class AuthController {

    private final KeycloakAuthService keycloakAuthService;

    public AuthController(KeycloakAuthService keycloakAuthService) {
        this.keycloakAuthService = keycloakAuthService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String password = payload.get("password");

        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(false, "email and password are required", null));
        }

        try {
            Map<String, Object> tokenResponse = keycloakAuthService.loginWithPassword(email, password);

            if (tokenResponse == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "Invalid credentials or Keycloak error", null));
            }

            return ResponseEntity.ok(new ApiResponse(true, "Login successful", tokenResponse));

        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(new ApiResponse(false, "Keycloak error: " + e.getResponseBodyAsString(), null));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, "Unexpected error during login: " + e.getMessage(), null));
        }
    }

    @GetMapping("/verify")
    public ResponseEntity<ApiResponse> verify(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(false, "Missing or invalid Authorization header", null));
        }

        String token = authHeader.substring(7);

        try {
            Map<String, Object> introspect = keycloakAuthService.introspectToken(token);

            if (introspect == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, "Introspection failed", null));
            }

            Boolean active = (Boolean) introspect.getOrDefault("active", false);
            if (!active) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "Token is not active", Map.of("active", false)));
            }

            /* ---------------- Extract Required Fields ---------------- */
            String name = (String) introspect.getOrDefault("name", "");
            String preferredUsername = (String) introspect.getOrDefault("preferred_username", "");
            String givenName = (String) introspect.getOrDefault("given_name", "");
            String familyName = (String) introspect.getOrDefault("family_name", "");
            String email = (String) introspect.getOrDefault("email", "");
            String username = (String) introspect.getOrDefault("username", preferredUsername);

            // Default token_type to Bearer
            String tokenType = "Bearer";

            /* ---------------- Determine Role ---------------- */
            String role = "USER";
            Object realmAccess = introspect.get("realm_access");

            if (realmAccess instanceof Map) {
                Object rolesObj = ((Map<?, ?>) realmAccess).get("roles");
                if (rolesObj instanceof Iterable) {
                    for (Object r : (Iterable<?>) rolesObj) {
                        if ("ADMIN".equalsIgnoreCase(String.valueOf(r))) {
                            role = "ADMIN";
                            break;
                        }
                    }
                }
            }

            /* ---------------- Prepare Final Response Body ---------------- */
            Map<String, Object> data = Map.of("active", true, "name", name, "preferred_username", preferredUsername, "given_name", givenName, "family_name", familyName, "email", email, "username", username, "token_type", tokenType);

            return ResponseEntity.ok(new ApiResponse(true, "Token is valid", Map.of("data", data, "role", role)));

        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(new ApiResponse(false, "Keycloak error: " + e.getResponseBodyAsString(), null));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, "Unexpected error during token verification: " + e.getMessage(), null));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(@RequestBody Map<String, String> payload) {

        String refreshToken = payload.get("token");

        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(false, "refresh token missing in body", null));
        }

        try {
            boolean ok = keycloakAuthService.logoutByRefreshToken(refreshToken);

            if (!ok) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(false, "Invalid or expired refresh token. Logout failed.", null));
            }

            return ResponseEntity.ok(new ApiResponse(true, "Logout successful", null));

        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(new ApiResponse(false, "Keycloak error: " + e.getResponseBodyAsString(), null));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, "Unexpected error during logout: " + e.getMessage(), null));
        }
    }
}