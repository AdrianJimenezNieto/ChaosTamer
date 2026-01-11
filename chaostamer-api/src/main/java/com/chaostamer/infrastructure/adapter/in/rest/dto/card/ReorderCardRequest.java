package com.chaostamer.infrastructure.adapter.in.rest.dto.card;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class ReorderCardRequest {
  @NotNull private Long cardId;
  @NotNull private Long newTaskListId;
  @NotNull private Integer newCardOrder;
}
