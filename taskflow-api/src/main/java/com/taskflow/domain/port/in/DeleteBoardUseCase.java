package com.taskflow.domain.port.in;

public interface DeleteBoardUseCase {

    void deleteBoard(Long boardId, String username);
}
