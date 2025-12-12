package com.taskflow.domain.port.out;

import com.taskflow.domain.model.TaskList;
import java.util.List;
import java.util.Optional;

public interface TaskListRepositoryPort {
  
  List<TaskList> findAllByBoard(Long boardId);

  TaskList save(TaskList taskList);

  Optional<TaskList> findById(Long taskListId);

  void deleteById(Long id);
}
