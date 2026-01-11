package com.chaostamer.domain.port.in.tasklist;

import com.chaostamer.domain.model.TaskList;
import com.chaostamer.infrastructure.adapter.in.rest.dto.tasklist.UpdateTaskListRequest;

public interface UpdateTaskListUseCase {
    TaskList updateTaskList(Long taskListId, UpdateTaskListRequest request, String username);
}
