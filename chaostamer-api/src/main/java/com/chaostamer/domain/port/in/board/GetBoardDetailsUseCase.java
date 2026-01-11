package com.chaostamer.domain.port.in.board;

import com.chaostamer.domain.model.Board;

public interface GetBoardDetailsUseCase {

  // Returns a complet Board
  // Recives the ID of the Board and the email of the user for security
  Board getBoardDetails(Long boardId, String username);
}
