package com.taskflow.infrastructure.adapter.out.persistence;

import com.taskflow.domain.model.Card;
import com.taskflow.domain.port.out.CardRepositoryPort;
import com.taskflow.infrastructure.adapter.out.persistence.entity.CardEntity;
import com.taskflow.infrastructure.adapter.out.persistence.entity.TaskListEntity;
import com.taskflow.infrastructure.adapter.out.persistence.mapper.CardPersistenceMapper;
import com.taskflow.infrastructure.adapter.out.persistence.repository.CardJpaRepository;
import com.taskflow.infrastructure.adapter.out.persistence.repository.TaskListJpaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CardPersistenceAdapter implements CardRepositoryPort{
  
  // Dependencies inyection
  private final CardJpaRepository cardJpaRepository;
  private final TaskListJpaRepository taskListJpaRepository;
  private final CardPersistenceMapper cardMapper;

  @Override
  public List<Card> findAllByTaskListId(Long taskListId) {
    return cardJpaRepository.findAllByTaskListId(taskListId)
      .stream()
      .map(cardMapper::toDomain)
      .collect(Collectors.toList());
  }

  @Override
  public List<Card> findAllByIds(Set<Long> cardsIds) {
    return cardJpaRepository.findAllById(cardsIds)
      .stream()
      .map(cardMapper::toDomain)
      .collect(Collectors.toList());
  }

  @Override
  public Card save(Card card) {
    // Map to entity
    CardEntity cardEntity = cardMapper.toEntity(card);
    // Get owner entity
    TaskListEntity taskListEntity = taskListJpaRepository.findById(card.getTaskListId())
    .orElseThrow(() -> new EntityNotFoundException());
    // Assign to the entity
    cardEntity.setTaskList(taskListEntity);
    // Persist on the db
    CardEntity savedEntity = cardJpaRepository.save(cardEntity);
    // Remap and return
    return cardMapper.toDomain(savedEntity);
  }
}
