package com.chaostamer.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCardRequest {
    @Size(min = 1, max = 100, message = "El título debe tener entre 1 y 100 caracteres")
    private String title;

    @Size(max = 500, message = "La descripción no puede exceder los 500 caracteres")
    private String description;
}
