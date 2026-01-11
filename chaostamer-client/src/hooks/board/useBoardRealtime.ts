import { useCallback } from "react";
import { useBoardWebSocket } from "../../hooks/board/useBoardWebSocket";
import type { BoardDetails, Card } from "../../types/board.types";
import type { CardMovePayload, ReorderTaskListRequest } from "../../types/api.types";

interface UseBoardRealtimeReturn {
  sendCardMove: (payload: CardMovePayload) => void;
}

export const useBoardRealtime = (
  boardId: string | undefined,
  setBoard: React.Dispatch<React.SetStateAction<BoardDetails | null>>
): UseBoardRealtimeReturn => {

  const handleWebSocketEvent = useCallback((event: any) => {
    // CARD_MOVE
    if (event.type === 'CARD_MOVE') {
      const payload = event.payload as CardMovePayload;

      setBoard((prev) => {
        if (!prev) return null;

        const currentDestList = prev.lists.find(l => l.id === payload.targetListId);
        const cardInDest = currentDestList?.cards.find(c => c.id === payload.cardId);

        // Ignore card if it is in the right place
        if (cardInDest && currentDestList?.cards.indexOf(cardInDest) === payload.newPosition) {
          return prev;
        }

        console.log("Recibido movimiento remoto:", payload);

        // State movement logic
        let movedCard: Card | undefined;
        const newLists = prev.lists.map(list => {
          const card = list.cards.find(c => c.id === payload.cardId);
          if (card) {
            movedCard = { ...card }; // Clone the card
            return { ...list, cards: list.cards.filter(c => c.id !== payload.cardId) };
          }
          return list;
        });

        if (!movedCard) return prev; // Abort if the card was not found

        // Insert the found card into the destiny list
        return {
          ...prev,
          lists: newLists.map(list => {
            if (list.id === payload.targetListId) {
              const newCards = [...list.cards];
              newCards.splice(payload.newPosition, 0, movedCard!);
              return { ...list, cards: newCards };
            }
            return list;
          })
        };
      });
    }

    // CARD_CREATE
    if (event.type === 'CARD_CREATE') {
      const newCard = event.payload as Card;
      console.log("Recibida nueva tarjeta remota:", newCard);

      setBoard((prev) => {
        if (!prev) return null;

        // Anti-echo validation
        const alreadyExists = prev.lists.some(list =>
          list.cards.some(c => c.id === newCard.id)
        );

        if (alreadyExists) return prev;

        // Insertion
        return {
          ...prev,
          lists: prev.lists.map(list => {
            if (list.id === newCard.taskListId) {
              return { ...list, cards: [...list.cards, newCard] };
            }
            return list;
          })
        }
      })
    }

    // CARD_DELETE
    if (event.type === 'CARD_DELETE') {
      const deletedCardId = event.payload as number;
      console.log("🗑️ Eliminada tarjeta remota ID:", deletedCardId);

      setBoard((prev) => {
        if (!prev) return null;
        return {
          ...prev,
          lists: prev.lists.map(list => ({
            ...list,
            cards: list.cards.filter(c => c.id !== deletedCardId)
          }))
        };
      });
    }

    // LIST_MOVE
    if (event.type === 'LIST_MOVE') {
      const changes = event.payload as ReorderTaskListRequest[];
      console.log('Reordenando listas remotas:', changes);

      setBoard((prev) => {
        if (!prev) return null;

        // Create a fast access map
        const orderMap = new Map(changes.map(c => [c.taskListId, c.newListOrder]));

        // Clone and assign the new orders
        const newLists = prev.lists.map(list => {
          if (orderMap.has(list.id)) {
            return { ...list, listOrder: orderMap.get(list.id)! };
          }
          return list;
        });

        // Order the array based on 'listOrder'
        newLists.sort((a, b) => a.listOrder - b.listOrder);

        return { ...prev, lists: newLists };
      })
    }
  }, [setBoard]);

  // Initialize Hook
  const { sendCardMove } = useBoardWebSocket(boardId, handleWebSocketEvent);

  return { sendCardMove };
}
