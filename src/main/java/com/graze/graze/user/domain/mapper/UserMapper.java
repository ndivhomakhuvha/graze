package com.graze.graze.user.domain.mapper;

import com.graze.graze.user.domain.User;
import com.graze.graze.user.domain.dto.UserDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

  UserDto toDto(User user);
}
