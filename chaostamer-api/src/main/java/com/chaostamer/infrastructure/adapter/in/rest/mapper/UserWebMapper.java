package com.chaostamer.infrastructure.adapter.in.rest.mapper;

import org.springframework.stereotype.Component;

import com.chaostamer.domain.model.User;
import com.chaostamer.infrastructure.adapter.in.rest.dto.auth.RegisterUserRequest;
import com.chaostamer.infrastructure.adapter.in.rest.dto.auth.UserResponse;

@Component
public class UserWebMapper {
  
  // Converts RegisterUserRequest DTO to User domain model
  public User toDomain(RegisterUserRequest request) {

    return User.builder()
            .name(request.getName())
            .lastName(request.getLastName())
            .email(request.getEmail())
            .password(request.getPassword())
            .build();
  }

  public UserResponse toResponse(User domain) {
    UserResponse response = new UserResponse();
    response.setId(domain.getId());
    response.setName(domain.getName());
    response.setLastName(domain.getLastName());
    response.setEmail(domain.getEmail());
    return response;
  }
}
