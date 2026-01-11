package com.chaostamer.infrastructure.adapter.in.rest.dto.auth;

import lombok.Data;

// Clean DTO that only exposes what frontend needs
@Data
public class UserResponse {
  private Long id;
  private String name;
  private String lastName;
  private String email;
}
