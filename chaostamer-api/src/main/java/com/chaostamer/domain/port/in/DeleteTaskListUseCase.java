package com.chaostamer.domain.port.in;

public interface DeleteTaskListUseCase {
    void deleteTaskList(Long taskListId, String username);
}
