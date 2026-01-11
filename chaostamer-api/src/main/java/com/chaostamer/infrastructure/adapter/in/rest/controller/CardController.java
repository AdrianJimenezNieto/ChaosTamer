package com.chaostamer.infrastructure.adapter.in.rest.controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.chaostamer.domain.model.Card;
import com.chaostamer.domain.model.TaskList;
import com.chaostamer.domain.port.in.card.DeleteCardUseCase;
import com.chaostamer.domain.port.in.card.UpdateCardUseCase;
import com.chaostamer.domain.port.out.CardRepositoryPort;
import com.chaostamer.domain.port.out.TaskListRepositoryPort;
import com.chaostamer.infrastructure.adapter.in.rest.dto.card.CardResponse;
import com.chaostamer.infrastructure.adapter.in.rest.dto.card.UpdateCardRequest;
import com.chaostamer.infrastructure.adapter.in.rest.mapper.CardWebMapper;
import com.chaostamer.infrastructure.adapter.in.websocket.dto.WsEvent;

@RestController
@RequestMapping("/api/v1/cards")
@RequiredArgsConstructor
public class CardController {
    
    // Dependecies inyection
    private final UpdateCardUseCase updateCardUseCase;
    private final DeleteCardUseCase deleteCardUseCase;
    private final CardWebMapper cardWebMapper;

    // WS inyections
    private final SimpMessagingTemplate messagingTemplate;
    private final CardRepositoryPort cardRepositoryPort;
    private final TaskListRepositoryPort taskListRepositoryPort;

    @PatchMapping("/{cardId}")
    public ResponseEntity<CardResponse> updateCard (
        @PathVariable Long cardId,
        @Valid @RequestBody UpdateCardRequest request,
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        // Call the use case
        Card updatedCard = updateCardUseCase.updateCard(cardId, request, userDetails.getUsername());

        // Convert the domain object into response DTO
        CardResponse response = cardWebMapper.toResponse(updatedCard);

        // Return a 200 OK with the new data
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{cardId}")
    public ResponseEntity<Void> deleteCard(
        @PathVariable Long cardId,
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        // Search the info of the card before deleting
        Long boardId = null;
        try {
            Card card = cardRepositoryPort.findById(cardId).orElseThrow(() -> new EntityNotFoundException("Tarjeta no encontrada"));
            if (card != null) {
                TaskList list = taskListRepositoryPort.findById(card.getTaskListId()).orElseThrow(() -> new EntityNotFoundException("Lista no encontrada"));
                if (list != null) {
                    boardId = list.getBoardId();
                }
            }
        } catch (Exception e) { }

        // Delete the card
        deleteCardUseCase.deleteCard(cardId, userDetails.getUsername());

        // WebSocket notification
        if (boardId != null) {
            try {
                WsEvent<Long> event = WsEvent.<Long>builder()
                    .type("CARD_DELETE")
                    .boardId(boardId)
                    .payload(cardId)
                    .build();

                // Send the notification
                messagingTemplate.convertAndSend("/topic/board/" + boardId, event);
            } catch (Exception e) {
                System.err.println("Error WS Delete: " + e.getMessage());
            }
        }

        // Return a 204 No Content
        return ResponseEntity.noContent().build();
    }
}
