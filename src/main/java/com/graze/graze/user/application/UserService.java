package com.graze.graze.user.application;

import com.graze.graze.user.domain.dto.UserDto;
import com.graze.graze.user.domain.mapper.UserMapper;
import com.graze.graze.user.domain.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  public UserService(UserRepository userRepository, UserMapper userMapper) {
    this.userRepository = userRepository;
    this.userMapper = userMapper;
  }
}
