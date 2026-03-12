package com.graze.graze.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Converts Keycloak client roles from the JWT token's {@code roles} claim
 * (mapped via a Keycloak protocol mapper) into Spring Security {@link GrantedAuthority}
 * instances using the {@code ROLE_} prefix convention.
 *
 * <p>Falls back to extracting roles from the standard {@code resource_access} claim
 * if the top-level {@code roles} claim is absent.
 */
public class KeycloakRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

  private final String clientId;

  public KeycloakRoleConverter(String clientId) {
    this.clientId = clientId;
  }

  @Override
  public Collection<GrantedAuthority> convert(Jwt jwt) {
    // Primary: roles claim mapped by the Keycloak "Client Roles" protocol mapper
    List<String> roles = jwt.getClaimAsStringList("roles");
    if (roles != null && !roles.isEmpty()) {
      return roles.stream()
        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
        .collect(Collectors.toList());
    }

    // Fallback: standard resource_access claim
    Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
    if (resourceAccess == null) {
      return List.of();
    }

    @SuppressWarnings("unchecked")
    Map<String, Object> clientAccess = (Map<String, Object>) resourceAccess.get(clientId);
    if (clientAccess == null) {
      return List.of();
    }

    @SuppressWarnings("unchecked")
    List<String> clientRoles = (List<String>) clientAccess.get("roles");
    if (clientRoles == null) {
      return List.of();
    }

    return clientRoles.stream()
      .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
      .collect(Collectors.toList());
  }
}

