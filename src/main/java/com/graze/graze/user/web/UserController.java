package com.graze.graze.user.web;

import com.graze.graze.user.application.UserService;
import com.graze.graze.user.domain.dto.UserDto;
import com.graze.graze.user.domain.dto.UserRegistrationRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping("/register")
  @ResponseStatus(HttpStatus.CREATED)
  public UserDto register(@Valid @RequestBody UserRegistrationRequest request) {
    return userService.register(request);
  }
}
