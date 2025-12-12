package com.taskflow.domain.port.out;

import com.taskflow.domain.model.Board;
import java.util.List;
import java.util.Optional;

public interface BoardRepositoryPort {
  
  // save board
  Board save(Board board);

  // Find all tasklists
  List<Board> findAllByUserId(Long userId);

  Optional<Board> findById(Long boardId);

  void deleteById(Long id);
}
