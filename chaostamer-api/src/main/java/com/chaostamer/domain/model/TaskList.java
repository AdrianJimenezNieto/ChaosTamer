package com.chaostamer.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data // Generates getters, setters, toString, equals, hashCode
@Builder // Implements the builder pattern
@NoArgsConstructor
@AllArgsConstructor
public class TaskList {
  private Long id;
  private String title;
  private Integer listOrder;
  // reference to the parent board
  private Long boardId;
  private List<Card> cards;
}
