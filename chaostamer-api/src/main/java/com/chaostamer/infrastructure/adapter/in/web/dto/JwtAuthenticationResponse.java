package com.chaostamer.infrastructure.adapter.in.web.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class JwtAuthenticationResponse {

  private final String token;
  private final String tokenType = "Bearer";
  
}
