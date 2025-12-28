package com.chaostamer.infrastructure.adapter.in.web.dto;

import lombok.Data;
import lombok.Builder;
import java.util.List;

@Data
@Builder
public class BoardDetailResponse {
  private Long id;
  private String title;
  private List<TaskListResponse> lists; // <-- Nested
}
