package com.chaostamer.domain.port.out;

import java.util.List;
import java.util.Optional;

import com.chaostamer.domain.model.Board;

public interface BoardRepositoryPort {
  
  // save board
  Board save(Board board);

  // Find all tasklists
  List<Board> findAllByUserId(Long userId);

  Optional<Board> findById(Long boardId);

  void deleteById(Long id);
}
