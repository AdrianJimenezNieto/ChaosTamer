package com.taskflow.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Generates getters, setters, toString, equals, hashCode
@Builder // Implements the builder pattern
@NoArgsConstructor
@AllArgsConstructor
public class Card {
  private Long id;
  private String title;
  private String description;
  // reference to the parent task list
  private Long taskListId;
  private Integer cardOrder; // drag-and-drop feature
}
