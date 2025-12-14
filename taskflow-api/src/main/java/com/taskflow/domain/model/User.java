package com.taskflow.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Generates getters, setters, toString, equals, hashCode
@Builder // Implements the builder pattern
@NoArgsConstructor
@AllArgsConstructor
public class User {
  private Long id;
  private String name;
  private String lastName;
  private String email;
  private String password;
  // we manage the relationships from the persistence layer
}
