package com.chaostamer.domain.port.in;

import java.util.List;

import com.chaostamer.infrastructure.adapter.in.web.dto.ReorderTaskListRequest;

public interface ReorderTaskListUseCase {
    void reorderTaskLists (List<ReorderTaskListRequest> requests, String username);
}
