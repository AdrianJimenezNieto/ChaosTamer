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
public class Board {
  private Long id;
  private String title;
  // reference to the owner user
  private Long userId;
  private List<TaskList> lists;
}
