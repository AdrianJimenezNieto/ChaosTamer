package com.chaostamer.infrastructure.adapter.in.rest.dto.card;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateCardRequest {
  @NotBlank(message = "El titulo no puede estar vacio")  
  private String title;
}
