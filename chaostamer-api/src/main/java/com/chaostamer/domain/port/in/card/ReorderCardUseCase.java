package com.chaostamer.domain.port.in.card;

import java.util.List;

import com.chaostamer.infrastructure.adapter.in.rest.dto.card.ReorderCardRequest;

public interface ReorderCardUseCase {
  
  void reorderCards(List<ReorderCardRequest> updates, String username);
}
