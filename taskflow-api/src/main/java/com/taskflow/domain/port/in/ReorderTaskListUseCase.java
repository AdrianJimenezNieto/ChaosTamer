package com.taskflow.domain.port.in;

import com.taskflow.infrastructure.adapter.in.web.dto.ReorderTaskListRequest;
import java.util.List;

public interface ReorderTaskListUseCase {
    void reorderTaskLists (List<ReorderTaskListRequest> requests, String username);
}
