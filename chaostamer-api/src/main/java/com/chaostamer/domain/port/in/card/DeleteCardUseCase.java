package com.chaostamer.domain.port.in.card;

public interface DeleteCardUseCase {
    /**
     * Delete a card and reorder the result list.
     * @param cardId ID of the card to delete.
     * @param username User that request the delete (security check).
     */
    void deleteCard(Long cardId, String username);
}
