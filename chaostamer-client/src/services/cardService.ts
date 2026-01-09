import api from "../lib/axios";
import type { Card } from "../types/board.types";
import type { ReorderCardRequest, UpdateCardRequest } from "../types/api.types";

// US-203: Create new card on a list
export const createCard = async(taskListId: number, title: String): Promise<Card> => {
  try{
    const response = await api.post<Card>(`/tasklists/${taskListId}/cards`, { title });
    return response.data;
  } catch (error) {
    console.error("Error creating the card: ", error);
    throw new Error("No se pudo crear la tarjeta")
  }
};

// US-301: Reorder cards of a board
export const reorderCardsPersistence = async(updates: ReorderCardRequest[]): Promise<void> => {
  try { 
    // Call the API
    api.put(`/tasklists/cards/reorder`, updates);
  } catch (error) {
    throw new Error("No se pudo persistir el nuevo orden de las tarjetas");
  }
};

// US-206: Update Card Details
export const updateCard = async(cardId: number, data: UpdateCardRequest): Promise<Card> => {
  try {
    // Call the API
    const response = await api.patch<Card>(`/cards/${cardId}`, data);
    return response.data;
  } catch (error) {
    console.error("Error creating card: ", error);
    throw new Error("No se pudo actualizar la tarjeta.");
  }
};

// US-209: Delete a card
export const deleteCard = async(cardId: number): Promise<void> => {
  try{
    await api.delete(`/cards/${cardId}`);
    console.log("🗑️ Tarjeta eliminada correctamente.");
  } catch (error) {
    console.error("Error al eliminar la tarjeta: ", error);
    throw new Error("No se pudo eliminar la tarjeta");
  }
};