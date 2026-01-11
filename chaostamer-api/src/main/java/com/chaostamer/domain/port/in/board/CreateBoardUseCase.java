package com.chaostamer.domain.port.in.board;

import com.chaostamer.domain.model.Board;

import lombok.Getter;

public interface CreateBoardUseCase {

  Board createBoard(CreateBoardCommand command, String ownerUsername);

  @Getter
  class CreateBoardCommand {
    private final String title;
    // Constructor
    public CreateBoardCommand(String title) {
      if(title == null || title.isBlank()) {
        throw new IllegalArgumentException("El título no puede estar vacío.");
      }
      this.title = title;
    }
  }
  
}
