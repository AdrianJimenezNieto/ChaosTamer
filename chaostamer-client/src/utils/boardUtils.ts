import type { BoardDetails } from "../types/board.types";

export const cleanBoardState = (board: BoardDetails): BoardDetails => {
  if (!board || !board.lists) {
    return board;
  }

  return {
    ...board,
    lists: board.lists.map(list => ({
      ...list,
      cards: (list.cards || []).filter(c => c),
    }))
  }
}

export const parseId = (id: string | number): number => {
  if (typeof id === 'number') return id;
  return Number(id.replace('list-', '').replace('card-', ''));
}
