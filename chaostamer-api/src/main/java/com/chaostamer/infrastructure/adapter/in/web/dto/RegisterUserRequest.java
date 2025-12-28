package com.chaostamer.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterUserRequest {
  
  // Nottations for validation that review the incoming data

  @NotBlank(message = "El nombre no puede estar vacío.")
  private String name;

  private String lastName;

  @NotBlank(message = "El email no puede estar vacío.")
  @Email(message = "El email no es válido.")
  private String email;

  @NotBlank(message = "La contraseña no puede estar vacía.")
  @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres.")
  private String password;
}
