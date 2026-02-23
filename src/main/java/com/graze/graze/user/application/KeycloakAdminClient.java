package com.graze.graze.user.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

@Component
class KeycloakAdminClient {

  private static final Logger log = LoggerFactory.getLogger(KeycloakAdminClient.class);

  @Value("${graze.keycloak.admin-server-url:http://localhost:8180}")
  private String adminServerUrl;

  @Value("${graze.keycloak.realm:graze-realm}")
  private String realm;

  @Value("${graze.keycloak.admin-client-id:admin-cli}")
  private String adminClientId;

  @Value("${graze.keycloak.admin-username:admin}")
  private String adminUsername;

  @Value("${graze.keycloak.admin-password:admin}")
  private String adminPassword;

  private final RestClient restClient;
  private final ReentrantLock tokenLock = new ReentrantLock();

  private String cachedToken;
  private Instant tokenExpiry = Instant.MIN;

  KeycloakAdminClient(RestClient.Builder builder) {
    this.restClient = builder.build();
  }

  /**
   * Returns a valid admin token, reusing a cached token if it has not yet expired.
   */
  private String getAdminToken() {
    tokenLock.lock();
    try {
      if (cachedToken != null && Instant.now().isBefore(tokenExpiry)) {
        return cachedToken;
      }
      MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
      form.add("grant_type", "password");
      form.add("client_id", adminClientId);
      form.add("username", adminUsername);
      form.add("password", adminPassword);

      @SuppressWarnings("unchecked")
      Map<String, Object> response = restClient.post()
        .uri(adminServerUrl + "/realms/master/protocol/openid-connect/token")
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .body(form)
        .retrieve()
        .body(Map.class);

      if (response == null || !response.containsKey("access_token")) {
        throw new IllegalStateException("Failed to obtain admin token from Keycloak");
      }
      cachedToken = (String) response.get("access_token");
      int expiresIn = response.containsKey("expires_in")
        ? ((Number) response.get("expires_in")).intValue() : 60;
      // Refresh 10 seconds before actual expiry to avoid edge-cases
      tokenExpiry = Instant.now().plusSeconds(Math.max(expiresIn - 10, 0));
      return cachedToken;
    } finally {
      tokenLock.unlock();
    }
  }

  /**
   * Creates a user in Keycloak and returns the new user's Keycloak ID.
   * The user is created with {@code emailVerified=false} and a required
   * {@code VERIFY_EMAIL} action so that a verification email is sent.
   *
   * @return the Keycloak user ID extracted from the Location response header
   * @throws IllegalStateException if the user could not be created
   */
  String createUser(String username, String email, String firstName, String lastName, String password) {
    String token = getAdminToken();

    Map<String, Object> credential = Map.of(
      "type", "password",
      "value", password,
      "temporary", false
    );

    Map<String, Object> userRepresentation = Map.of(
      "username", username,
      "email", email,
      "firstName", firstName,
      "lastName", lastName,
      "enabled", true,
      "emailVerified", false,
      "credentials", List.of(credential),
      "requiredActions", List.of("VERIFY_EMAIL")
    );

    var response = restClient.post()
      .uri(adminServerUrl + "/admin/realms/" + realm + "/users")
      .header("Authorization", "Bearer " + token)
      .contentType(MediaType.APPLICATION_JSON)
      .body(userRepresentation)
      .retrieve()
      .toBodilessEntity();

    String location = response.getHeaders().getFirst("Location");
    if (location == null) {
      throw new IllegalStateException("Keycloak did not return a Location header after user creation");
    }
    return location.substring(location.lastIndexOf('/') + 1);
  }

  /**
   * Triggers Keycloak to send a verification email to the specified user.
   * Failures are logged but do not abort registration so the user record is
   * not lost; the admin can resend the email later.
   */
  void sendVerificationEmail(String keycloakUserId) {
    try {
      String token = getAdminToken();
      restClient.put()
        .uri(adminServerUrl + "/admin/realms/" + realm + "/users/" + keycloakUserId + "/send-verify-email")
        .header("Authorization", "Bearer " + token)
        .retrieve()
        .toBodilessEntity();
    } catch (RestClientException e) {
      log.warn("Failed to send verification email to Keycloak user {}: {}", keycloakUserId, e.getMessage());
    }
  }
}
