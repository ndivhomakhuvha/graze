package com.graze.graze.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI / Swagger configuration.
 *
 * <p>Registers a Bearer JWT security scheme so that the Swagger UI exposes an
 * "Authorize" button. Users must supply a valid Keycloak-issued JWT before any
 * protected endpoint will respond with a successful status code. The Swagger UI
 * itself is also protected by {@link SecurityConfig} and requires a valid token
 * to be accessed at all.
 */
@Configuration
public class SwaggerConfig {

  private static final String BEARER_AUTH = "bearerAuth";

  @Bean
  public OpenAPI openAPI() {
    return new OpenAPI()
      .info(new Info()
        .title("Graze API")
        .description("Livestock management API – authentication via Keycloak JWT is required")
        .version("v1"))
      .addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH))
      .components(new Components()
        .addSecuritySchemes(BEARER_AUTH,
          new SecurityScheme()
            .name(BEARER_AUTH)
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT")
            .description("Provide a valid Keycloak JWT. Obtain a token from your Keycloak realm before using this UI.")));
  }
}
