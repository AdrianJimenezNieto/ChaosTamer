package com.taskflow.infrastructure.adapter.in.web.mapper;

import com.taskflow.domain.model.Board;
import com.taskflow.domain.model.TaskList;
import com.taskflow.domain.port.in.CreateBoardUseCase;
import com.taskflow.infrastructure.adapter.in.web.dto.CreateBoardRequest;
import com.taskflow.infrastructure.adapter.in.web.dto.BoardResponse;
import com.taskflow.infrastructure.adapter.in.web.dto.BoardDetailResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BoardWebMapper {

  private final TaskListWebMapper taskListWebMapper;
  
  // from DTO to Command 
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
      .sorted(Comparator.comparing(Board::getId))
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
        board.getLists().stream()
        .sorted(Comparator.comparing(TaskList::getListOrder))
        .map(taskListWebMapper::toResponse)
        .collect(Collectors.toList()) :
        List.of() // returns empty list
      )
      .build();
  }
}
