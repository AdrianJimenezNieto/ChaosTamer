package com.chaostamer.domain.port.in;

import java.util.List;

import com.chaostamer.infrastructure.adapter.in.web.dto.ReorderCardRequest;

public interface ReorderCardUseCase {
  
  void reorderCards(List<ReorderCardRequest> updates, String username);
}
