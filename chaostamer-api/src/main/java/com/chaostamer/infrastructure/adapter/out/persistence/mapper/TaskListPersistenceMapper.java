package com.chaostamer.infrastructure.adapter.out.persistence.mapper;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.chaostamer.domain.model.Card;
import com.chaostamer.domain.model.TaskList;
import com.chaostamer.infrastructure.adapter.out.persistence.entity.TaskListEntity;

@Component
@RequiredArgsConstructor
public class TaskListPersistenceMapper {

  private final CardPersistenceMapper cardMapper;
  
  public TaskList toDomain(TaskListEntity entity) {
    if (entity == null) return null;
    return TaskList.builder()
            .id(entity.getId())
            .title(entity.getTitle())
            .boardId(entity.getBoard() != null ? entity.getBoard().getId() : null)
            .listOrder(entity.getListOrder())
            .cards(
                entity.getCards() != null ?
                entity.getCards().stream()
                  .map(cardMapper::toDomain)
                  .sorted(Comparator.comparing(Card::getCardOrder))
                  .collect(Collectors.toList()) :
                new ArrayList<>()
                  )
            .build();
  }

  public TaskListEntity toEntity(TaskList domain) {
    if (domain == null) return null;
    TaskListEntity entity = new TaskListEntity();
    entity.setId(domain.getId());
    entity.setTitle(domain.getTitle());
    entity.setListOrder(domain.getListOrder());
    // boardId assigned on the adapter
    return entity;
  }
}
