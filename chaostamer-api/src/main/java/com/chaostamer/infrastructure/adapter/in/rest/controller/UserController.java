package com.chaostamer.infrastructure.adapter.in.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import com.chaostamer.domain.model.User;
import com.chaostamer.domain.port.in.auth.GetUserUseCase;
import com.chaostamer.infrastructure.adapter.in.rest.dto.auth.UserResponse;
import com.chaostamer.infrastructure.adapter.in.rest.mapper.UserWebMapper;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.http.ResponseEntity;

@RestController // Marks this class as a REST controller
@RequestMapping("/api/v1/users") // Base URL for user-related endpoints
@RequiredArgsConstructor
public class UserController {

  private final GetUserUseCase getUserUseCase;
  private final UserWebMapper userWebMapper;

  // Testing jwt validation token system
  @GetMapping("/me")
  public ResponseEntity<UserResponse> getMyProfile(@AuthenticationPrincipal UserDetails userDetails) {
    // Get the user
    User user = getUserUseCase.getUser(userDetails.getUsername());
    
    return ResponseEntity.ok(userWebMapper.toResponse(user));
  }
}
