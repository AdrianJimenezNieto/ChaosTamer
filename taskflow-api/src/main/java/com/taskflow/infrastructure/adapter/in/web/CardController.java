package com.taskflow.infrastructure.adapter.in.web;

import com.taskflow.domain.model.Card;
import com.taskflow.domain.port.in.UpdateCardUseCase;
import com.taskflow.infrastructure.adapter.in.web.dto.CardResponse;
import com.taskflow.infrastructure.adapter.in.web.dto.UpdateCardRequest;
import com.taskflow.infrastructure.adapter.in.web.mapper.CardWebMapper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cards")
@RequiredArgsConstructor
public class CardController {
    
    // Dependecies inyection
    private final UpdateCardUseCase updateCardUseCase;
    private final CardWebMapper cardWebMapper;

    @PutMapping("/{cardId}")
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
}
