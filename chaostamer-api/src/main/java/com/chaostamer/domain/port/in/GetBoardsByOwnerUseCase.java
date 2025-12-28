package com.chaostamer.domain.port.in;

import java.util.List;

import com.chaostamer.domain.model.Board;

public interface GetBoardsByOwnerUseCase {
  List<Board> getBoards(String ownerUsername);
}
