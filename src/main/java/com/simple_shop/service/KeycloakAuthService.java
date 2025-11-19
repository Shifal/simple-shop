package com.simple_shop.service;

import com.simple_shop.config.KeycloakProperties;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class KeycloakAuthService {

    private final KeycloakProperties props;
    private final RestTemplate restTemplate;

    public KeycloakAuthService(KeycloakProperties props, RestTemplate restTemplate) {
        this.props = props;
        this.restTemplate = restTemplate;
    }

    private String tokenUrl() {
        return props.getServerUrl() + "/realms/" + props.getRealm() + "/protocol/openid-connect/token";
    }

    private String introspectUrl() {
        return props.getServerUrl() + "/realms/" + props.getRealm() + "/protocol/openid-connect/token/introspect";
    }

    private String revokeUrl() {
        return props.getServerUrl() + "/realms/" + props.getRealm() + "/protocol/openid-connect/revoke";
    }

    private String logoutUrl() {
        return props.getServerUrl() + "/realms/" + props.getRealm() + "/protocol/openid-connect/logout";
    }

    public Map<String, Object> loginWithPassword(String email, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "password");
        form.add("client_id", props.getClientId());

        if (props.getClientSecret() != null && !props.getClientSecret().isBlank()) {
            form.add("client_secret", props.getClientSecret());
        }

        form.add("username", email);
        form.add("password", password);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(form, headers);

        try {
            ResponseEntity<Map> resp = restTemplate.postForEntity(tokenUrl(), request, Map.class);
            if (resp.getStatusCode() == HttpStatus.OK) {
                return resp.getBody();
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Map<String, Object> introspectToken(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("token", token);
        form.add("client_id", props.getClientId());
        if (props.getClientSecret() != null && !props.getClientSecret().isBlank()) {
            form.add("client_secret", props.getClientSecret());
        }

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(form, headers);

        try {
            ResponseEntity<Map> resp = restTemplate.postForEntity(introspectUrl(), request, Map.class);
            if (resp.getStatusCode() == HttpStatus.OK) {
                return resp.getBody();
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean logoutByRefreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) return false;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("refresh_token", refreshToken);
        form.add("client_id", props.getClientId());
        if (props.getClientSecret() != null && !props.getClientSecret().isBlank()) {
            form.add("client_secret", props.getClientSecret());
        }

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(form, headers);

        try {
            ResponseEntity<String> resp = restTemplate.postForEntity(logoutUrl(), request, String.class);

            return resp.getStatusCode().is2xxSuccessful();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
