package com.taskflow.infrastructure.adapter.in.web.mapper;

import com.taskflow.domain.model.Card;
import com.taskflow.domain.port.in.CreateCardUseCase;
import com.taskflow.infrastructure.adapter.in.web.dto.CardResponse;
import com.taskflow.infrastructure.adapter.in.web.dto.CreateCardRequest;

import org.springframework.stereotype.Component;

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
