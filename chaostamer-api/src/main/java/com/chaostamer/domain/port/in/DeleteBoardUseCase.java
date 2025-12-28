package com.chaostamer.domain.port.in;

public interface DeleteBoardUseCase {

    void deleteBoard(Long boardId, String username);
}
