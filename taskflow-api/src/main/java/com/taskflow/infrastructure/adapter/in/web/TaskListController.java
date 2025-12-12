package com.taskflow.infrastructure.adapter.in.web;

import com.taskflow.domain.model.Card;
import com.taskflow.domain.model.TaskList;

import com.taskflow.domain.port.in.CreateCardUseCase;
import com.taskflow.domain.port.in.ReorderCardUseCase;
import com.taskflow.domain.port.in.UpdateTaskListUseCase;
import com.taskflow.domain.port.in.DeleteTaskListUseCase;
import com.taskflow.infrastructure.adapter.in.web.dto.CardResponse;
import com.taskflow.infrastructure.adapter.in.web.dto.CreateCardRequest;
import com.taskflow.infrastructure.adapter.in.web.dto.ReorderCardRequest;
import com.taskflow.infrastructure.adapter.in.web.dto.UpdateTaskListRequest;
import com.taskflow.infrastructure.adapter.in.web.dto.TaskListResponse;

import com.taskflow.infrastructure.adapter.in.web.mapper.CardWebMapper;
import com.taskflow.infrastructure.adapter.in.web.mapper.TaskListWebMapper;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasklists") // <-- new base route
@RequiredArgsConstructor
public class TaskListController {
  
  // dependencies inyection
  private final CreateCardUseCase createCardUseCase;
  private final ReorderCardUseCase reorderCardsUseCase;
  private final UpdateTaskListUseCase updateTaskListUseCase;
  private final DeleteTaskListUseCase deleteTaskListUseCase;

  private final TaskListWebMapper taskListWebMapper;
  private final CardWebMapper cardWebMapper;

  // US - 203: Create new card in a tasklist
  @PostMapping("/{listId}/cards")
  public ResponseEntity<CardResponse> createCard(
    @PathVariable Long listId,
    @Valid @RequestBody CreateCardRequest request,
    @AuthenticationPrincipal UserDetails userDetails
  ) {
    // Map DTO into Command
    CreateCardUseCase.CreateCardCommand command = cardWebMapper.toCommand(request, listId);

    // Call the use case
    Card newCard = createCardUseCase.createCard(command, userDetails.getUsername());

    // Mapp the result (domain) into response DTO
    return new ResponseEntity<>(cardWebMapper.toResponse(newCard), HttpStatus.CREATED);
  }

  // Reorder cards endpoint
  @PutMapping("/cards/reorder")
  public ResponseEntity<Void> reorderCards(
    @Valid @RequestBody List<ReorderCardRequest> updates,
    @AuthenticationPrincipal UserDetails userDetails
  ) {
    // Call the use case to process the array changes
    reorderCardsUseCase.reorderCards(updates, userDetails.getUsername());

    // 204 No Content: Standard response for an update
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/{taskListId}")
  public ResponseEntity<TaskListResponse> updateTaskList(
    @PathVariable Long taskListId,
    @Valid @RequestBody UpdateTaskListRequest request,
    @AuthenticationPrincipal UserDetails userDetails
  ) {
    TaskList updatedList = updateTaskListUseCase.updateTaskList(taskListId, request, userDetails.getUsername());

    return ResponseEntity.ok(taskListWebMapper.toResponse(updatedList));
  }

  // US-208: Delete a tasklist
  @DeleteMapping("/{taskListId}")
  public ResponseEntity<Void> deleteTaskList(
    @PathVariable Long taskListId,
    @AuthenticationPrincipal UserDetails userDetails
  ) {
    deleteTaskListUseCase.deleteTaskList(taskListId, userDetails.getUsername());

    return ResponseEntity.noContent().build();
  }
}
