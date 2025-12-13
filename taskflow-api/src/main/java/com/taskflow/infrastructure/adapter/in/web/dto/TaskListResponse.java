package com.taskflow.infrastructure.adapter.in.web.dto;

import lombok.Data;
import lombok.Builder;
import java.util.List;

@Data
@Builder
public class TaskListResponse {
  private Long id;
  private String title;
  private Integer listOrder;
  private List<CardResponse> cards; // <-- nested
}
