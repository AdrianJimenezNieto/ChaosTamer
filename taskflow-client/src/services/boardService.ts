import api from '../lib/axios';
import type { Board, BoardDetails, TaskList, Card, ReorderCardRequest, UpdateCardRequest } from '../models';

// US-106: Bring all of my boards
export const getMyBoards = async (): Promise<Board[]> => {
  try {
    const response = await api.get<Board[]>('/boards');
    return response.data;
  } catch (error) {
    console.error("Error fetching boards: ", error);
    throw new Error("No se pudieron cargar los tableros.")
  }
};

// US-105: Create new board
export const createBoard = async (title: string): Promise<Board> => {
  try {
    const response = await api.post<Board>('/boards', { title });
    return response.data;
  } catch (error) {
    console.error("Error creating board: ", error);
    throw new Error("Fallo al crear el tablero.")
  }
};

// US-201: Get Board details
export const getBoardDetails = async (boardId: string): Promise<BoardDetails> => {
  try {
    // Call the endpoint
    const response = await api.get<BoardDetails>(`/boards/${boardId}`);
    return response.data;
  } catch (error) {
    console.error('Error fetching board details: ', error);
    throw new Error('No se pudo cargar el tablero')
  }
};

// US-202: Create new TaskList
export const createTaskList = async (boardId: string, title: string): Promise<TaskList> => {
  try{
    // Call the endpoint
    const response = await api.post<TaskList>(`/boards/${boardId}/lists`, { title });
    return response.data;
  } catch (error) {
    console.error("Error creating the task list: ", error);
    throw new Error('No se pudo crear la lista')
  }
};

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