package com.chaostamer.domain.port.in.tasklist;

import java.util.List;

import com.chaostamer.infrastructure.adapter.in.rest.dto.tasklist.ReorderTaskListRequest;

public interface ReorderTaskListUseCase {
    void reorderTaskLists (List<ReorderTaskListRequest> requests, String username);
}
