package com.chaostamer.infrastructure.adapter.in.rest.dto.board;

import lombok.Data;
import lombok.Builder;
import java.util.List;

import com.chaostamer.infrastructure.adapter.in.rest.dto.tasklist.TaskListResponse;

@Data
@Builder
public class BoardDetailResponse {
  private Long id;
  private String title;
  private List<TaskListResponse> lists; // <-- Nested
}
