package com.chaostamer.infrastructure.adapter.in.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.chaostamer.domain.model.Card;
import com.chaostamer.domain.port.in.DeleteCardUseCase;
import com.chaostamer.domain.port.in.UpdateCardUseCase;
import com.chaostamer.infrastructure.adapter.in.web.dto.CardResponse;
import com.chaostamer.infrastructure.adapter.in.web.dto.UpdateCardRequest;
import com.chaostamer.infrastructure.adapter.in.web.mapper.CardWebMapper;

@RestController
@RequestMapping("/api/v1/cards")
@RequiredArgsConstructor
public class CardController {
    
    // Dependecies inyection
    private final UpdateCardUseCase updateCardUseCase;
    private final DeleteCardUseCase deleteCardUseCase;
    private final CardWebMapper cardWebMapper;

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
        deleteCardUseCase.deleteCard(cardId, userDetails.getUsername());

        // Return a 204 No Content
        return ResponseEntity.noContent().build();
    }
}
