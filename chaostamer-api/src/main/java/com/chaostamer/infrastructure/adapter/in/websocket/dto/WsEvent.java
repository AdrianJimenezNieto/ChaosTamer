package com.chaostamer.infrastructure.adapter.in.websocket.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WsEvent<T> {
  private String type;  // Ex: "CARD_MOVE"
  private Long boardId; // To know what 'topic' send to
  private T payload;    // Data
}
