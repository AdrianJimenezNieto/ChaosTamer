package com.taskflow.infrastructure.adapter.in.web;

import com.taskflow.domain.model.Card;
import com.taskflow.domain.port.in.CreateCardUseCase;
import com.taskflow.domain.port.in.ReorderCardUseCase;
import com.taskflow.infrastructure.adapter.in.web.dto.CardResponse;
import com.taskflow.infrastructure.adapter.in.web.dto.CreateCardRequest;
import com.taskflow.infrastructure.adapter.in.web.dto.ReorderCardRequest;
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
@RequestMapping("/api/v1/tasklists") // <-- new base route
@RequiredArgsConstructor
public class TaskListController {
  
  // dependencies inyection
  private final CreateCardUseCase createCardUseCase;
  private final ReorderCardUseCase reorderCardsUseCase;
  private final BoardWebMapper boardWebMapper;

  // US - 203: Create new card in a tasklist
  @PostMapping("/{listId}/cards")
  public ResponseEntity<CardResponse> createCard(
    @PathVariable Long listId,
    @Valid @RequestBody CreateCardRequest request,
    @AuthenticationPrincipal UserDetails userDetails
  ) {
    // Map DTO into Command
    CreateCardUseCase.CreateCardCommand command = boardWebMapper.toCommand(request, listId);

    // Call the use case
    Card newCard = createCardUseCase.createCard(command, userDetails.getUsername());

    // Mapp the result (domain) into response DTO
    return new ResponseEntity<>(boardWebMapper.toCardResponse(newCard), HttpStatus.CREATED);
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
}
