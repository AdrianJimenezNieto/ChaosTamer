package com.chaostamer.domain.port.in;

import com.chaostamer.domain.model.TaskList;

import lombok.Getter;

public interface CreateTaskListUseCase {
  
  TaskList createTaskList(CreateTaskListCommand command, String ownerUsername);

  @Getter
  class CreateTaskListCommand {
    private final String title;
    private final Long boardId;

    public CreateTaskListCommand(String title, Long boardId) {
      if (title == null || title.isBlank()){
        throw new IllegalArgumentException("El título no puede estar vacío");
      }
      if (boardId == null) {
        throw new IllegalArgumentException("El ID del tablero no puede ser nulo");
      }
      this.title = title;
      this.boardId = boardId;
    }
  }
}
