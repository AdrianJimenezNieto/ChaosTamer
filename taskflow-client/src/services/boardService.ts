import api from '../lib/axios';
import type { Board, BoardDetails, TaskList, Card } from '../models';

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