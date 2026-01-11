package com.chaostamer.domain.port.in.tasklist;

public interface DeleteTaskListUseCase {
    void deleteTaskList(Long taskListId, String username);
}
