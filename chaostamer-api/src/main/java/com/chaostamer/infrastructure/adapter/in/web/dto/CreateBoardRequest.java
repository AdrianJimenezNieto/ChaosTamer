package com.chaostamer.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateBoardRequest {
  @NotBlank(message = "El título es obligatorio.")
  private String title;
}
