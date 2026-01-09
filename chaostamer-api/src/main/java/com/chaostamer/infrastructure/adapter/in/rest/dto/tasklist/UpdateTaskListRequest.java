package com.chaostamer.infrastructure.adapter.in.rest.dto.tasklist;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UpdateTaskListRequest {
    
    @NotBlank(message="El título es obligatorio")
    @Size(min = 1, max = 100, message = "El título debe tener entre 1 y 50 caracteres")
    private String title;
}
