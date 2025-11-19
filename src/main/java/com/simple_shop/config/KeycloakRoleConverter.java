package com.simple_shop.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.*;
import java.util.stream.Collectors;

public class KeycloakRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Object realmAccess = jwt.getClaim("realm_access");
        if (!(realmAccess instanceof Map)) return Collections.emptyList();

        Map<?,?> ra = (Map<?,?>) realmAccess;
        Object rolesObj = ra.get("roles");
        if (!(rolesObj instanceof Collection)) return Collections.emptyList();

        Collection<?> roles = (Collection<?>) rolesObj;
        return roles.stream()
                .map(Object::toString)
                .map(r -> "ROLE_" + r) // Spring Security requires ROLE_ prefix
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
