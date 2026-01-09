package com.chaostamer.domain.port.in.board;

public interface DeleteBoardUseCase {

    void deleteBoard(Long boardId, String username);
}
