package com.taskflow.domain.port.in;

import com.taskflow.infrastructure.adapter.in.web.dto.ReorderCardRequest;
import java.util.List;

public interface ReorderCardUseCase {
  
  void reorderCards(List<ReorderCardRequest> updates, String username);
}
