package com.graze.graze.user.application;

import com.graze.graze.user.domain.User;
import com.graze.graze.user.domain.mapper.UserMapper;
import com.graze.graze.user.domain.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  public UserService(UserRepository userRepository, UserMapper userMapper) {
    this.userRepository = userRepository;
    this.userMapper = userMapper;
  }

  /**
   * Resolves a local {@link User} from a Keycloak JWT.
   *
   * <ol>
   *   <li>Try by {@code keycloak_id} (the JWT {@code sub} claim).</li>
   *   <li>Fall back to matching by {@code preferred_username} or {@code email}
   *       and link the Keycloak ID so future look-ups are instant.</li>
   *   <li>If no local user exists at all, create one from the JWT claims.</li>
   * </ol>
   */
  @Transactional
  public User resolveFromJwt(Jwt jwt) {
    String keycloakId = jwt.getSubject();

    // 1. Direct keycloak_id match
    var byKeycloakId = userRepository.findByKeycloakId(keycloakId);
    if (byKeycloakId.isPresent()) {
      return byKeycloakId.get();
    }

    // 2. Try matching by username (preferred_username claim)
    String username = jwt.getClaimAsString("preferred_username");
    if (username != null) {
      var byUsername = userRepository.findByUsername(username);
      if (byUsername.isPresent()) {
        User user = byUsername.get();
        user.setKeycloakId(keycloakId);
        return userRepository.save(user);
      }
    }

    // 3. Try matching by email
    String email = jwt.getClaimAsString("email");
    if (email != null) {
      var byEmail = userRepository.findByEmail(email);
      if (byEmail.isPresent()) {
        User user = byEmail.get();
        user.setKeycloakId(keycloakId);
        return userRepository.save(user);
      }
    }

    // 4. No existing user – auto-create from JWT claims
    User newUser = new User();
    newUser.setKeycloakId(keycloakId);
    newUser.setUsername(username != null ? username : keycloakId);
    newUser.setEmail(email != null ? email : keycloakId + "@unknown");
    newUser.setFirstName(jwt.getClaimAsString("given_name"));
    newUser.setLastName(jwt.getClaimAsString("family_name"));
    newUser.setEmailVerified(Boolean.TRUE.equals(jwt.getClaimAsBoolean("email_verified")));
    return userRepository.save(newUser);
  }
}
