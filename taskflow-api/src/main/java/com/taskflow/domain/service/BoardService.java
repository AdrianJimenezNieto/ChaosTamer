package com.taskflow.domain.service;

import com.taskflow.domain.model.Board;
import com.taskflow.domain.model.User;
import com.taskflow.domain.model.Card;
import com.taskflow.domain.model.TaskList;

import com.taskflow.domain.port.in.CreateBoardUseCase;
import com.taskflow.domain.port.in.GetBoardDetailsUseCase;
import com.taskflow.domain.port.in.GetBoardsByOwnerUseCase;
import com.taskflow.domain.port.in.UpdateBoardUseCase;
import com.taskflow.domain.port.in.DeleteBoardUseCase;

import com.taskflow.domain.port.out.BoardRepositoryPort;
import com.taskflow.domain.port.out.CardRepositoryPort;
import com.taskflow.domain.port.out.TaskListRepositoryPort;
import com.taskflow.domain.port.out.UserRepositoryPort;

import com.taskflow.infrastructure.adapter.in.web.dto.UpdateBoardRequest;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService implements 
  CreateBoardUseCase,
  GetBoardsByOwnerUseCase,
  GetBoardDetailsUseCase,
  UpdateBoardUseCase,
  DeleteBoardUseCase {
  
  private final BoardRepositoryPort boardRepositoryPort;
  private final UserRepositoryPort userRepositoryPort;
  private final TaskListRepositoryPort taskListRepositoryPort;
  private final CardRepositoryPort cardRepositoryPort;

  @Override
  public Board createBoard(CreateBoardCommand command, String ownerUsername) {
    // Find the owner of the board
    User owner = userRepositoryPort.findByEmail(ownerUsername)
                                    .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado."));
    // Create the Board object
    Board newBoard = Board.builder()
                          .title(command.getTitle())
                          .userId(owner.getId())
                          .build();
    // Save it using the persistence port
    return boardRepositoryPort.save(newBoard);
  }

  @Override
  public List<Board> getBoards(String ownerUsername) {
    // Find the user 
    User owner = userRepositoryPort.findByEmail(ownerUsername)
                                    .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado."));
    // Ask for all of tasklists for that userId
    return boardRepositoryPort.findAllByUserId(owner.getId());
  }

  @Override
  public Board getBoardDetails(Long boardId, String username) {
    // Find the user with the port (security check)
    User user = userRepositoryPort.findByEmail(username)
      .orElseThrow(() -> new EntityNotFoundException());
    // Find the board
    Board board = boardRepositoryPort.findById(boardId)
      .orElseThrow(() -> new EntityNotFoundException());

    // SECURITY CHECK be sure that the board is owned by the user
    if(!board.getUserId().equals(user.getId())) {
      throw new AccessDeniedException("No tienes permiso para ver este tablero");
    }

    // Load the lists of that board
    List<TaskList> lists = taskListRepositoryPort.findAllByBoard(boardId);

    // Load the cards for each taskList
    for (TaskList list : lists) {
      List<Card> cards = cardRepositoryPort.findAllByTaskListId(list.getId());
      list.setCards(cards);
    }

    // Assign the lists to the board
    board.setLists(lists);

    // Return the board
    return board;
  }

  // US-204: Update the title of a board
  @Override
  @Transactional
  public Board updateBoard(Long boardId, UpdateBoardRequest request, String username) {
    // Get the board
    Board boardToUpdate = boardRepositoryPort.findById(boardId)
      .orElseThrow(() -> new EntityNotFoundException("No se ha encontrado el tablero"));

      
    // SECURITY
    User user = userRepositoryPort.findByEmail(username)
    .orElseThrow(() -> new EntityNotFoundException("No se ha encontrado el usuario"));

    if (!boardToUpdate.getUserId().equals(user.getId())) {
      throw new AccessDeniedException("No tienes permisos para editar este tablero");
    }

    // Update the board
    boardToUpdate.setTitle(request.getTitle());

    // Save changes and return
    return boardRepositoryPort.save(boardToUpdate);
  }

  // US-207: Delete board
  public void deleteBoard(Long boardId, String username) {
    // get the board
    Board board = boardRepositoryPort.findById(boardId)
      .orElseThrow(() -> new EntityNotFoundException("Tablero no encontrado"));
    
    // SECURITY
    User user = userRepositoryPort.findByEmail(username)
      .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
    if (!board.getUserId().equals(user.getId())) {
      throw new AccessDeniedException("No tienes permiso para eliminar este tablero");
    }

    // Delete the board (on cascade)
    boardRepositoryPort.deleteById(boardId);
  }
}