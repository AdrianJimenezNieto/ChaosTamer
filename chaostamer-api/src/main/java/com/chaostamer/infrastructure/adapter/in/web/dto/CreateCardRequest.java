package com.chaostamer.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateCardRequest {
  @NotBlank(message = "El titulo no puede estar vacio")  
  private String title;
}
