import type { Card } from "./board.types"; 

// --- REST API REQUESTS ---

export type ReorderCardRequest = {
  cardId: number;
  newTaskListId: number;
  newCardOrder: number;
};

export type UpdateCardRequest = {
  title?: string;
  description?: string;
};

export type UpdateTaskListRequest = {
  title: string;
}

export type UpdateBoardRequest = {
  title: string;
}

export type ReorderTaskListRequest = {
  taskListId: number;
  newListOrder: number;
}

// --- WEBSOCKET TYPES ---

export interface CardMovePayload {
  cardId: number;
  sourceListId: number;
  targetListId: number;
  newPosition: number;
}

export type WsEvent = 
  | { type: 'CARD_MOVE'; boardId: number; payload: CardMovePayload }
  | { type: 'CARD_CREATE'; boardId: number; payload: Card }
  | { type: 'CARD_DELETE'; boardId: number; payload: number }
  | { type: 'LIST_MOVE'; boardId: number; payload: ReorderTaskListRequest[] };