package com.graze.graze.user.domain.mapper;

import com.graze.graze.user.domain.User;
import com.graze.graze.user.domain.dto.UserDto;
import com.graze.graze.user.domain.dto.UserRegistrationRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "keycloakId", ignore = true)
  @Mapping(target = "emailVerified", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  User toUser(UserRegistrationRequest request);

  UserDto toDto(User user);
}
