package com.graze.graze.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

  @Value("${graze.cors.allowed-origins:http://localhost:4200}")
  private String corsAllowedOrigins;

  @Value("${graze.keycloak.client-id:graze-app}")
  private String keycloakClientId;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
      .cors(cors -> cors.configurationSource(corsConfigurationSource()))
      // CSRF protection is disabled because this API uses stateless JWT Bearer token
      // authentication. Tokens must be explicitly set in the Authorization header, so
      // they cannot be sent by a cross-site request without JavaScript access (which
      // is prevented by CORS), making CSRF attacks inapplicable.
      .csrf(csrf -> csrf.disable())
      .sessionManagement(session ->
        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .headers(headers -> headers
        .contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'none'; frame-ancestors 'none'"))
        .referrerPolicy(referrer -> {})
      )
      .authorizeHttpRequests(auth -> auth
        .requestMatchers("/actuator/health").permitAll()
        .requestMatchers(
          "/v3/api-docs/**",
          "/swagger-ui/**",
          "/swagger-ui.html"
        ).authenticated()

        // Animal endpoints
        .requestMatchers(HttpMethod.GET, "/animals/**").hasRole("view-animal")
        .requestMatchers(HttpMethod.POST, "/animals/**").hasRole("manage-animal")
        .requestMatchers(HttpMethod.PUT, "/animals/**").hasRole("manage-animal")
        .requestMatchers(HttpMethod.DELETE, "/animals/**").hasRole("manage-animal")

        // Health-record endpoints
        .requestMatchers(HttpMethod.GET, "/health-records/**").hasRole("view-health")
        .requestMatchers(HttpMethod.POST, "/health-records/**").hasRole("manage-health")
        .requestMatchers(HttpMethod.PUT, "/health-records/**").hasRole("manage-health")
        .requestMatchers(HttpMethod.PATCH, "/health-records/**").hasRole("manage-health")
        .requestMatchers(HttpMethod.DELETE, "/health-records/**").hasRole("manage-health")

        // Treatment endpoints
        .requestMatchers(HttpMethod.GET, "/treatments/**").hasRole("view-health")
        .requestMatchers(HttpMethod.POST, "/treatments/**").hasRole("manage-health")
        .requestMatchers(HttpMethod.PUT, "/treatments/**").hasRole("manage-health")
        .requestMatchers(HttpMethod.DELETE, "/treatments/**").hasRole("manage-health")

        // Finance endpoints
        .requestMatchers(HttpMethod.GET, "/finances/**").hasRole("view-finances")
        .requestMatchers(HttpMethod.POST, "/finances/**").hasRole("manage-finances")
        .requestMatchers(HttpMethod.PUT, "/finances/**").hasRole("manage-finances")
        .requestMatchers(HttpMethod.DELETE, "/finances/**").hasRole("manage-finances")

        // Deny everything else by default
        .anyRequest().denyAll()
      )
      .oauth2ResourceServer(oauth2 -> oauth2
        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
      );
    return http.build();
  }

  @Bean
  public JwtAuthenticationConverter jwtAuthenticationConverter() {
    JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
    converter.setJwtGrantedAuthoritiesConverter(new KeycloakRoleConverter(keycloakClientId));
    return converter;
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(Arrays.asList(corsAllowedOrigins.split(",")));
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
    config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));
    config.setAllowCredentials(true);
    config.setMaxAge(3600L);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
  }
}
