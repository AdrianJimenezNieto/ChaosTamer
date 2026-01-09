package com.chaostamer.infrastructure.adapter.in.web;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.chaostamer.domain.model.Card;
import com.chaostamer.domain.model.TaskList;
import com.chaostamer.domain.port.in.CreateCardUseCase;
import com.chaostamer.domain.port.in.DeleteTaskListUseCase;
import com.chaostamer.domain.port.in.ReorderCardUseCase;
import com.chaostamer.domain.port.in.ReorderTaskListUseCase;
import com.chaostamer.domain.port.in.UpdateTaskListUseCase;
import com.chaostamer.domain.port.out.TaskListRepositoryPort;
import com.chaostamer.infrastructure.adapter.in.web.dto.CardResponse;
import com.chaostamer.infrastructure.adapter.in.web.dto.CreateCardRequest;
import com.chaostamer.infrastructure.adapter.in.web.dto.ReorderCardRequest;
import com.chaostamer.infrastructure.adapter.in.web.dto.ReorderTaskListRequest;
import com.chaostamer.infrastructure.adapter.in.web.dto.TaskListResponse;
import com.chaostamer.infrastructure.adapter.in.web.dto.UpdateTaskListRequest;
import com.chaostamer.infrastructure.adapter.in.web.dto.ws.WsEvent;
import com.chaostamer.infrastructure.adapter.in.web.mapper.CardWebMapper;
import com.chaostamer.infrastructure.adapter.in.web.mapper.TaskListWebMapper;

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
  private final ReorderTaskListUseCase reorderTaskListUseCase;

  private final SimpMessagingTemplate messagingTemplate;
  private final TaskListRepositoryPort taskListRepositoryPort;

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
    // Create the response 
    CardResponse response = cardWebMapper.toResponse(newCard);

    // WebSocket Logic
    try {
      Long boardId = taskListRepositoryPort.findById(listId)
        .map(list -> list.getBoardId())
        .orElseThrow(() -> new RuntimeException("Error de consistencia: Lista sin tablero"));

      // Get the event ready
      WsEvent<CardResponse> event = WsEvent.<CardResponse>builder()
        .type("CARD_CREATE")
        .boardId(boardId)
        .payload(response)
        .build();

        // Send the topic 
        String destination = "/topic/board/" + boardId;
        messagingTemplate.convertAndSend(destination, event);
    } catch (Exception e) {
      System.err.println("Error enviando notificación WS: " + e.getMessage());
    }

    // Final REST management
    return new ResponseEntity<>(response, HttpStatus.CREATED);
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

  // Reorder lists in a board
  @PutMapping("/reorder")
  public ResponseEntity<Void> reorderLists(
    @RequestBody List<ReorderTaskListRequest> requests,
    @AuthenticationPrincipal UserDetails userDetails
  ) {
    reorderTaskListUseCase.reorderTaskLists(requests, userDetails.getUsername());
    return ResponseEntity.noContent().build();
  }
}
