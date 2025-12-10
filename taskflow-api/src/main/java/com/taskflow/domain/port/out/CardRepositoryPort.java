package com.taskflow.domain.port.out;

import com.taskflow.domain.model.Card;
import java.util.List;
import java.util.Set;

public interface CardRepositoryPort {
  // US-201: Find all cards in a list
  List<Card> findAllByTaskListId(Long taskListId);
  
  // US-203: Save a new card
  Card save(Card card);

  List<Card> findAllByIds(Set<Long> cardsIds);
}
