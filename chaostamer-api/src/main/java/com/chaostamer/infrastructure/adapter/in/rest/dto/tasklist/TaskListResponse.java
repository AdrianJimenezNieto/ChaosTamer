package com.chaostamer.infrastructure.adapter.in.rest.dto.tasklist;

import lombok.Data;
import lombok.Builder;
import java.util.List;

import com.chaostamer.infrastructure.adapter.in.rest.dto.card.CardResponse;

@Data
@Builder
public class TaskListResponse {
  private Long id;
  private String title;
  private Integer listOrder;
  private List<CardResponse> cards; // <-- nested
}
