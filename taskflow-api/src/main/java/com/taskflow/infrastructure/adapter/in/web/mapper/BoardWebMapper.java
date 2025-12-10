package com.taskflow.infrastructure.adapter.in.web.mapper;

import com.taskflow.domain.model.Board;
import com.taskflow.domain.model.TaskList;
import com.taskflow.domain.model.Card;
import com.taskflow.domain.port.in.CreateBoardUseCase;
import com.taskflow.domain.port.in.CreateTaskListUseCase;
import com.taskflow.domain.port.in.CreateCardUseCase;
import com.taskflow.infrastructure.adapter.in.web.dto.CreateBoardRequest;
import com.taskflow.infrastructure.adapter.in.web.dto.CreateTaskListRequest;
import com.taskflow.infrastructure.adapter.in.web.dto.BoardResponse;
import com.taskflow.infrastructure.adapter.in.web.dto.BoardDetailResponse;
import com.taskflow.infrastructure.adapter.in.web.dto.CardResponse;
import com.taskflow.infrastructure.adapter.in.web.dto.TaskListResponse;
import com.taskflow.infrastructure.adapter.in.web.dto.CreateCardRequest;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BoardWebMapper {
  
  // from DTO to Command of the use case
  public CreateBoardUseCase.CreateBoardCommand toCommand(CreateBoardRequest request) {
    return new CreateBoardUseCase.CreateBoardCommand(request.getTitle());
  }

  // From domain into DTO (Response)
  public BoardResponse toResponse(Board board) {
    return BoardResponse.builder()
      .id(board.getId())
      .title(board.getTitle())
      .build();
  }

  // From List of Domain into a DTO List (Response)
  public List<BoardResponse> toResponseList(List<Board> boards) {
    return boards.stream()
      .map(this::toResponse)
      .collect(Collectors.toList());
  }

  // From board to boardDetailsResponse
  public BoardDetailResponse toDetailResponse(Board board) {
    return BoardDetailResponse.builder()
      .id(board.getId())
      .title(board.getTitle())
      .lists(
        board.getLists() != null ?
        board.getLists().stream().map(this::toTaskListResponse).collect(Collectors.toList()) :
        List.of() // returns empty list
      )
      .build();
  }

  // From domain to DTO TaskList
  public TaskListResponse toTaskListResponse(TaskList list) {
    return TaskListResponse.builder()
      .id(list.getId())
      .title(list.getTitle())
      .cards(
        list.getCards() != null ?
        list.getCards().stream().map(this::toCardResponse).collect(Collectors.toList()) :
        List.of()
      )
      .build();
  }

  // From domain to DTO CardResponse
  public CardResponse toCardResponse(Card card) {
    return CardResponse.builder()
      .id(card.getId())
      .title(card.getTitle())
      .description(card.getDescription())
      .cardOrder(card.getCardOrder())
      .build();
  }

  // From request to command (CREATE TASKLIST)
  public CreateTaskListUseCase.CreateTaskListCommand toCommand(CreateTaskListRequest request, Long boardId) {
    return new CreateTaskListUseCase.CreateTaskListCommand(request.getTitle(), boardId);
  }

  // From request to command (CREATE CARD)
  public CreateCardUseCase.CreateCardCommand toCommand(CreateCardRequest request, Long taskListId){
    return new CreateCardUseCase.CreateCardCommand(request.getTitle(), taskListId);
  }
}
