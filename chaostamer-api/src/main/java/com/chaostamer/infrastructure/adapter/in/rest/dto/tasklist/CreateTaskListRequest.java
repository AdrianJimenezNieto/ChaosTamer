package com.chaostamer.infrastructure.adapter.in.rest.dto.tasklist;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateTaskListRequest {
  @NotBlank(message = "El título es obligatorio")
  private String title;
}
