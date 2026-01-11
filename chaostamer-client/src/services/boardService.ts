import api from '../lib/axios';
import type { Board, BoardDetails } from '../types/board.types';
import type { UpdateBoardRequest } from '../types/api.types';

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

// US-204: Update board title
export const updateBoard = async(boardId: number, data: UpdateBoardRequest): Promise<Board> => {
  try {
    // Call the endpoint
    const response = await api.patch<Board>(`/boards/${boardId}`, data);
    console.log("✅ Titulo del tablero actualizado");
    return response.data;
  } catch (error) {
    console.error("No se pudo editar el título del tablero", error);
    throw new Error("No se pudo editar el titulo del tablero")
  }
};

// US-207: Delete board
export const deleteBoard = async(boardId: number): Promise<void> => {
  try {
    // Call the endpoint to delete
    await api.delete(`/boards/${boardId}`);
    console.log("🗑️ Tablero borrado correctamente.");
  } catch (error) {
    console.error("No se ha podido eliminar el tablero: ", error);
    throw new Error("No ha sido posible borrar el tablero")
  }
};