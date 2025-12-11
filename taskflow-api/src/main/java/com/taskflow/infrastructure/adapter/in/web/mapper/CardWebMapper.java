package com.taskflow.infrastructure.adapter.in.web.mapper;

import com.taskflow.domain.model.Card;
import com.taskflow.infrastructure.adapter.in.web.dto.CardResponse;
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
}
