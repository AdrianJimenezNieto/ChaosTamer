package com.chaostamer.infrastructure.adapter.in.rest.dto.tasklist;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReorderTaskListRequest {
    
    @NotNull
    private Long taskListId;

    @NotNull
    private Integer newListOrder;
}
