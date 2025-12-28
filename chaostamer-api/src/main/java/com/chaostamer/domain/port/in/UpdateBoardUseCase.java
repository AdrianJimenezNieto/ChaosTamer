package com.chaostamer.domain.port.in;

import com.chaostamer.domain.model.Board;
import com.chaostamer.infrastructure.adapter.in.web.dto.UpdateBoardRequest;

public interface UpdateBoardUseCase {
    /**
     * Edits the title of a board
     * @param boardId Id of the board to edit
     * @param request JSON with the new title for the board
     * @param username The email of the user
     * @return The updated board
     */
    Board updateBoard(Long boardId, UpdateBoardRequest request, String username);
}
