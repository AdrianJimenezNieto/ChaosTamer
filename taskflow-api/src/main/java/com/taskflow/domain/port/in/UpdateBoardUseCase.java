package com.taskflow.domain.port.in;

import com.taskflow.infrastructure.adapter.in.web.dto.UpdateBoardRequest;
import com.taskflow.domain.model.Board;

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
