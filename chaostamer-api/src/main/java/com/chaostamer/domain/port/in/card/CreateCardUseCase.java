package com.chaostamer.domain.port.in.card;

import com.chaostamer.domain.model.Card;

import lombok.Getter;

public interface CreateCardUseCase {
  
  Card createCard(CreateCardCommand command, String ownerUsername);

  @Getter
  class CreateCardCommand {
    private final String title;
    private final Long taskListId;

    public CreateCardCommand(String title, Long taskListId) {
      if (title == null || title.isBlank()) {
        throw new IllegalArgumentException("El titulo no puede estar vacío");
      }
      if (taskListId == null) {
        throw new IllegalArgumentException("El ID de la lista no puede ser nulo");
      }

      this.title = title;
      this.taskListId= taskListId;
    }
  }
}
