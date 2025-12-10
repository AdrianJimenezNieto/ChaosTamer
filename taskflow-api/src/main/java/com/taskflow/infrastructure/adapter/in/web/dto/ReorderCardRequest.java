package com.taskflow.infrastructure.adapter.in.web.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class ReorderCardRequest {
  @NotNull private Long cardId;
  @NotNull private Long newTaskListId;
  @NotNull private Integer newCardOrder;
}
