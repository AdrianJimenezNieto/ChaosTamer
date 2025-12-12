package com.taskflow.infrastructure.adapter.in.web;

import com.taskflow.domain.model.Board;
import com.taskflow.domain.model.TaskList;

import com.taskflow.domain.port.in.CreateBoardUseCase;
import com.taskflow.domain.port.in.CreateTaskListUseCase;
import com.taskflow.domain.port.in.GetBoardsByOwnerUseCase;
import com.taskflow.domain.port.in.GetBoardDetailsUseCase;
import com.taskflow.domain.port.in.UpdateBoardUseCase;

import com.taskflow.infrastructure.adapter.in.web.dto.BoardResponse;
import com.taskflow.infrastructure.adapter.in.web.dto.CreateBoardRequest;
import com.taskflow.infrastructure.adapter.in.web.dto.BoardDetailResponse;
import com.taskflow.infrastructure.adapter.in.web.dto.CreateTaskListRequest;
import com.taskflow.infrastructure.adapter.in.web.dto.TaskListResponse;
import com.taskflow.infrastructure.adapter.in.web.dto.UpdateBoardRequest;

import com.taskflow.infrastructure.adapter.in.web.mapper.BoardWebMapper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/boards")
@RequiredArgsConstructor
public class BoardController {
  
  // Inyect the dependencies
  private final CreateBoardUseCase createBoardUseCase;
  private final GetBoardsByOwnerUseCase getBoardsByOwnerUseCase;
  private final GetBoardDetailsUseCase getBoardDetailsUseCase;
  private final CreateTaskListUseCase createTaskListUseCase;
  private final UpdateBoardUseCase updateBoardUseCase;
  private final BoardWebMapper boardWebMapper;

  // US-105: Create a board
  @PostMapping
  public ResponseEntity<BoardResponse> createBoard (
    @Valid @RequestBody CreateBoardRequest request,
    @AuthenticationPrincipal UserDetails userDetails
  ) {
    // Get the email (username) from the token
    String ownerUsername = userDetails.getUsername();
    // Map the dto into a command of domain
    CreateBoardUseCase.CreateBoardCommand command = boardWebMapper.toCommand(request);
    // Call the use case to create the board
    Board createBoard = createBoardUseCase.createBoard(command, ownerUsername);
    // Map the result into response DTO
    return new ResponseEntity<>(boardWebMapper.toResponse(createBoard), HttpStatus.CREATED);
  }

  // US-106: List the boards
  @GetMapping
  public ResponseEntity<List<BoardResponse>> getMyBoards (
    @AuthenticationPrincipal UserDetails userDetails // user of the token
  ) {
    String ownerUsername = userDetails.getUsername();
    // Call the use case to get the boards
    List<Board> boards = getBoardsByOwnerUseCase.getBoards(ownerUsername);
    // Map the list into DTO response and return it
    return ResponseEntity.ok(boardWebMapper.toResponseList(boards));
  }

  // US-201: Details of a board
  @GetMapping("/{boardId}")
  public ResponseEntity<BoardDetailResponse> getBoardDetails(
    @PathVariable Long boardId, // <-- Capture the Id from the URL 
    @AuthenticationPrincipal UserDetails userDetails // <-- user of the token
  ) {
    // Call the use case with the id and the email (username)
    Board board = getBoardDetailsUseCase.getBoardDetails(boardId, userDetails.getUsername());
    // Map the result into DTO response
    return ResponseEntity.ok(boardWebMapper.toDetailResponse(board));
  }

  // US-202: Create a new TaskList
  @PostMapping("/{boardId}/lists")
  public ResponseEntity<TaskListResponse> createList(
    @PathVariable Long boardId,
    @Valid @RequestBody CreateTaskListRequest request,
    @AuthenticationPrincipal UserDetails userDetails
  ){
    // Map the DTO into command
    CreateTaskListUseCase.CreateTaskListCommand command = boardWebMapper.toCommand(request, boardId);

    // Call the use case (includes sec check)
    TaskList newTaskList = createTaskListUseCase.createTaskList(command, userDetails.getUsername());

    // Map the domain result into response DTO 
    return new ResponseEntity<>(boardWebMapper.toTaskListResponse(newTaskList), HttpStatus.CREATED);
  }

  // US-204: Update a board
  @PatchMapping("/{boardId}")
  public ResponseEntity<BoardResponse> updateBoard(
    @PathVariable Long boardId,
    @Valid @RequestBody UpdateBoardRequest request, 
    @AuthenticationPrincipal UserDetails userDetails
  ) {
    // Call the service
    Board updateBoard = updateBoardUseCase.updateBoard(boardId, request, userDetails.getUsername());

    // Map the result and return a 200 OK
    return ResponseEntity.ok(boardWebMapper.toResponse(updateBoard));
  }
}
