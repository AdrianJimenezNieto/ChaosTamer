package com.chaostamer.infrastructure.adapter.in.web.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BoardResponse {
  private Long id;
  private String title;
  // dont return the userId, not necessary for the client
}
