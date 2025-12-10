package com.taskflow.infrastructure.adapter.out.persistence.mapper;

import com.taskflow.domain.model.Card;
import com.taskflow.infrastructure.adapter.out.persistence.entity.CardEntity;
import org.springframework.stereotype.Component;

@Component
public class CardPersistenceMapper {
  
  public Card toDomain(CardEntity entity) {
    if (entity == null) return null;
    return Card.builder()
      .id(entity.getId())
      .title(entity.getTitle())
      .description(entity.getDescription())
      .taskListId(entity.getTaskList() != null ? entity.getTaskList().getId() : null)
      .cardOrder(entity.getCardOrder())
      .build();
  }

  public CardEntity toEntity(Card domain) {
    if (domain == null) return null;
    CardEntity entity = new CardEntity();
    entity.setId(domain.getId());
    entity.setTitle(domain.getTitle());
    entity.setDescription(domain.getDescription());
    entity.setCardOrder(domain.getCardOrder());
    // TaskList on adapter
    return entity;
  }
}
