package com.graze.graze.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI / Swagger configuration.
 *
 * <p>Registers a Bearer JWT security scheme so that the Swagger UI exposes an
 * "Authorize" button and applies it globally to all endpoints. Users must
 * supply a valid Keycloak-issued JWT before any protected endpoint will
 * respond with a successful status code.
 */
@Configuration
public class SwaggerConfig {

  private static final String SECURITY_SCHEME_NAME = "bearerAuth";

  @Value("${app.base-url:http://localhost:8080}")
  private String baseUrl;

  @Bean
  public OpenAPI openAPI() {
    return new OpenAPI()
      .info(new Info()
        .title("Graze API")
        .description("Livestock management API – authentication via Keycloak JWT is required.")
        .version("v1")
        .contact(new Contact()
          .name("Graze Team")))
      .servers(List.of(
        new Server()
          .url(baseUrl)
          .description("Current environment")))
      .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
      .components(new Components()
        .addSecuritySchemes(SECURITY_SCHEME_NAME,
          new SecurityScheme()
            .name(SECURITY_SCHEME_NAME)
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT")
            .description("Provide a valid Keycloak JWT obtained from the graze-realm.")));
  }
}
