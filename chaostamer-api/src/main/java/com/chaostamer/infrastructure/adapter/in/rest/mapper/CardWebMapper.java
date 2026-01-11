package com.chaostamer.infrastructure.adapter.in.rest.mapper;

import org.springframework.stereotype.Component;

import com.chaostamer.domain.model.Card;
import com.chaostamer.domain.port.in.card.CreateCardUseCase;
import com.chaostamer.infrastructure.adapter.in.rest.dto.card.CardResponse;
import com.chaostamer.infrastructure.adapter.in.rest.dto.card.CreateCardRequest;

@Component
public class CardWebMapper {
    
    public CardResponse toResponse(Card card) {
        if (card == null) {
            return null;
        }

        return CardResponse.builder()
                .id(card.getId())
                .title(card.getTitle())
                .description(card.getDescription())
                .taskListId(card.getTaskListId())
                .cardOrder(card.getCardOrder())
                .build();
    }

    // From request to command (CREATE CARD)
  public CreateCardUseCase.CreateCardCommand toCommand(CreateCardRequest request, Long taskListId){
    return new CreateCardUseCase.CreateCardCommand(request.getTitle(), taskListId);
  }
}
