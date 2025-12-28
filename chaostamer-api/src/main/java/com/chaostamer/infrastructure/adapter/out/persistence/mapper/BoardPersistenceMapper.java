package com.chaostamer.infrastructure.adapter.out.persistence.mapper;

import org.springframework.stereotype.Component;

import com.chaostamer.domain.model.Board;
import com.chaostamer.infrastructure.adapter.out.persistence.entity.BoardEntity;

@Component
public class BoardPersistenceMapper {

  // from entity to domain model
  public Board toDomain(BoardEntity entity) {
    if (entity == null) {
      return null;
    }
    return Board.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .userId(entity.getOwner() != null ? entity.getOwner().getId() : null)
                .build();
  }

  // from domain to Entity (BBDD)
  public BoardEntity toEntity(Board domain) {
    if(domain == null) {
      return null;
    }
    BoardEntity entity = new BoardEntity();
    entity.setId(domain.getId());
    entity.setTitle(domain.getTitle());
    // Owner asigned on adapter cuz mapper should not be doing DataBase logic
    return entity;
  }
  
}
