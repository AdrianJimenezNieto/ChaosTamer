import { useState } from "react";
import { createTaskList, deleteTaskList, updateTaskList } from "../../services/taskListService";
import { deleteCard, updateCard } from "../../services/cardService";
import { updateBoard } from "../../services/boardService";
import type { BoardDetails, Card } from "../../types/board.types";
import { cleanBoardState } from "../../utils/boardUtils";

interface UseBoardOperationsReturn {
  createList: (title: string, boardId: string) => Promise<void>;
  deleteList: (listId: number) => Promise<void>;
  updateListTitle: (listId: number, newTitle: string) => Promise<void>;
  updateBoardTitle: (newTitle: string) => Promise<void>;
  saveCard: (cardId: number, newTitle: string, newDescription: string) => Promise<void>;
  deleteCardOp: (cardId: number) => Promise<void>;
  onCardAdded: (listId: number, newCard: Card) => void;
  operationError: string | null;
}

export const useBoardOperations = (
  board: BoardDetails | null,
  setBoard: React.Dispatch<React.SetStateAction<BoardDetails | null>>
): UseBoardOperationsReturn => {

  const [operationError, setOperationError] = useState<string | null>(null);

  const createList = async (title: string, boardId: string) => {
    if (!title.trim() || !boardId) return;

    try {
      const newList = await createTaskList(boardId, title);

      if (board) {
        setBoard({
          ...board,
          lists: [...board.lists, newList]
        });
      }
      setOperationError(null);
    } catch (err) {
      if (err instanceof Error) setOperationError(err.message);
    }
  };

  const deleteList = async (listId: number) => {
    if (!board) return;
    const oldBoard = { ...board };

    // Optimistic update
    setBoard(prev => {
      if (!prev) return null;
      return {
        ...prev,
        lists: prev.lists.filter(l => l.id !== listId)
      }
    });

    try {
      await deleteTaskList(listId);
    } catch (error) {
      setBoard(cleanBoardState(oldBoard));
      setOperationError("Hubo un error al eliminar la lista");
    }
  }

  const updateListTitle = async (listId: number, newTitle: string) => {
    if (!board) return;
    const oldBoard = { ...board };

    setBoard(prev => {
      if (!prev) return null;
      return {
        ...prev,
        lists: prev.lists.map(list =>
          list.id === listId ? { ...list, title: newTitle } : list
        )
      };
    });

    try {
      await updateTaskList(listId, { title: newTitle });
    } catch (error) {
      setBoard(cleanBoardState(oldBoard));
      setOperationError("No se pudo actualizar el título");
    }
  }

  const updateBoardTitle = async (newTitle: string) => {
    if (!board) return;
    if (!newTitle.trim() || newTitle === board.title) return;

    const oldTitle = board.title;
    setBoard({ ...board, title: newTitle });

    try {
      await updateBoard(board.id, { title: newTitle });
    } catch (error) {
      console.error("Error al actualizar el titulo: ", error);
      setBoard({ ...board, title: oldTitle });
    }
  }

  const saveCard = async (cardId: number, newTitle: string, newDescription: string) => {
    if (!board) return;
    const oldBoard = { ...board };

    const optimisticState = { ...board };
    optimisticState.lists = optimisticState.lists.map(list => ({
      ...list,
      cards: list.cards.map(card =>
        card.id === cardId ? { ...card, title: newTitle, description: newDescription } : card
      )
    }));
    setBoard(cleanBoardState(optimisticState));

    try {
      await updateCard(cardId, { title: newTitle, description: newDescription });
    } catch (error) {
      setBoard(cleanBoardState(oldBoard));
      setOperationError("Error al actualizar la tarjeta");
      console.error(error);
    }
  }

  const deleteCardOp = async (cardId: number) => {
    if (!board) return;
    const oldBoard = { ...board };

    // 1. Optimistic Update
    const optimisticState = { ...board };
    optimisticState.lists = optimisticState.lists.map(list => ({
      ...list,
      cards: list.cards.filter(c => c.id !== cardId)
    }));
    setBoard(cleanBoardState(optimisticState));

    try {
      await deleteCard(cardId);
    } catch (error) {
      setBoard(cleanBoardState(oldBoard));
      setOperationError("Error al eliminar la tarjeta");
    }
  }

  const onCardAdded = (listId: number, newCard: Card) => {
    if (!board) return;
    const updateLists = board.lists.map((list) => {
      if (list.id === listId) {
        return { ...list, cards: [...list.cards, newCard] }
      }
      return list;
    });
    setBoard({ ...board, lists: updateLists })
  }

  return {
    createList,
    deleteList,
    updateListTitle,
    updateBoardTitle,
    saveCard,
    deleteCardOp,
    onCardAdded,
    operationError
  };
}
