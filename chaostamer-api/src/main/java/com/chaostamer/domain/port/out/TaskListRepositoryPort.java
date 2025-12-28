package com.chaostamer.domain.port.out;

import java.util.List;
import java.util.Optional;

import com.chaostamer.domain.model.TaskList;

public interface TaskListRepositoryPort {
  
  List<TaskList> findAllByBoardId(Long boardId);

  TaskList save(TaskList taskList);

  Optional<TaskList> findById(Long taskListId);

  void deleteById(Long id);
}
