package com.graze.graze.user.application;

import com.graze.graze.user.domain.User;
import com.graze.graze.user.domain.dto.UserDto;
import com.graze.graze.user.domain.dto.UserRegistrationRequest;
import com.graze.graze.user.domain.mapper.UserMapper;
import com.graze.graze.user.domain.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.CONFLICT;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final KeycloakAdminClient keycloakAdminClient;

  public UserService(UserRepository userRepository, UserMapper userMapper,
                     KeycloakAdminClient keycloakAdminClient) {
    this.userRepository = userRepository;
    this.userMapper = userMapper;
    this.keycloakAdminClient = keycloakAdminClient;
  }

  @Transactional
  public UserDto register(UserRegistrationRequest request) {
    if (userRepository.existsByUsername(request.getUsername())) {
      throw new ResponseStatusException(CONFLICT, "Username already taken");
    }
    if (userRepository.existsByEmail(request.getEmail())) {
      throw new ResponseStatusException(CONFLICT, "Email already registered");
    }

    // Create user in Keycloak first; any exception here aborts the transaction.
    String keycloakId = keycloakAdminClient.createUser(
      request.getUsername(),
      request.getEmail(),
      request.getFirstName(),
      request.getLastName(),
      request.getPassword()
    );

    keycloakAdminClient.sendVerificationEmail(keycloakId);

    User user = userMapper.toUser(request);
    user.setKeycloakId(keycloakId);
    User saved = userRepository.save(user);
    return userMapper.toDto(saved);
  }
}
