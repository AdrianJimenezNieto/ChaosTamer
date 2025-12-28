package com.chaostamer.domain.port.in;

import com.chaostamer.domain.model.TaskList;
import com.chaostamer.infrastructure.adapter.in.web.dto.UpdateTaskListRequest;

public interface UpdateTaskListUseCase {
    TaskList updateTaskList(Long taskListId, UpdateTaskListRequest request, String username);
}
