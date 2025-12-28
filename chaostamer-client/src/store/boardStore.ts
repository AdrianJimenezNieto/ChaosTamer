import { create } from 'zustand';
import type { Board } from '../models';

// Define the shape of our state
interface BoardState {
  boards: Board[]; // List of boards
  setBoards: (boards: Board[]) => void;
  addBoard: (board: Board) => void;
}

// Create the store
export const useBoardStore = create<BoardState>((set) => ({
  boards: [], // initial state: empty array
  setBoards: (boards) => set({ boards }),
  // add a new board (after creating one)
  addBoard: (board) => set((state) => ({
    boards: [...state.boards, board]
  })),
}))