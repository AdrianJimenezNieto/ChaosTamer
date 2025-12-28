package com.chaostamer.domain.port.in;

import com.chaostamer.domain.model.Card;
import com.chaostamer.infrastructure.adapter.in.web.dto.UpdateCardRequest;

public interface UpdateCardUseCase {
    /**
     * Update the details of an existing card
     * @param cardId The id of the card to modify
     * @param request New data (title and/or description)
     * @param username The user that request the change (authentication porpouses)
     * @return The updated card
     */
    Card updateCard(Long cardId, UpdateCardRequest request, String username);
}
