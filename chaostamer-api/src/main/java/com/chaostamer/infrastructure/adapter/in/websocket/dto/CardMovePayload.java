package com.chaostamer.infrastructure.adapter.in.websocket.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardMovePayload {
  private Long cardId;
  private Long sourceListId;
  private Long targetListId;
  private Integer newPosition;
}
