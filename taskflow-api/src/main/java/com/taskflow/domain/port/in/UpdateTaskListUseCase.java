package com.taskflow.domain.port.in;

import com.taskflow.domain.model.TaskList;
import com.taskflow.infrastructure.adapter.in.web.dto.UpdateTaskListRequest;

public interface UpdateTaskListUseCase {
    TaskList updateTaskList(Long taskListId, UpdateTaskListRequest request, String username);
}
