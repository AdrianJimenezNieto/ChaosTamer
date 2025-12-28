package com.chaostamer.domain.port.out;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.chaostamer.domain.model.Card;

public interface CardRepositoryPort {
  // US-201: Find all cards in a list
  List<Card> findAllByTaskListId(Long taskListId);
  
  // US-203: Save a new card
  Card save(Card card);

  List<Card> findAllByIds(Set<Long> cardsIds);

  Optional<Card> findById(Long id);

  // US-209: Delete a card
  void deleteById(Long id);
}
