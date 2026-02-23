package com.graze.graze.user.domain.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UserDto {

  private UUID id;
  private String keycloakId;
  private String username;
  private String email;
  private String firstName;
  private String lastName;
  private boolean emailVerified;
  private LocalDateTime createdAt;
}
