import { useState, useCallback } from "react";
import {
  useSensor,
  useSensors,
  PointerSensor,
  rectIntersection,
  closestCorners,
  type DragStartEvent,
  type DragOverEvent,
  type DragEndEvent,
  type CollisionDetection
} from '@dnd-kit/core';
import { arrayMove } from "@dnd-kit/sortable";

import type { BoardDetails, Card, TaskList } from "../../types/board.types";
import type { CardMovePayload } from "../../types/api.types";
import { reorderTaskList } from "../../services/taskListService";
import { cleanBoardState, parseId } from "../../utils/boardUtils";

interface UseBoardDragAndDropReturn {
  activeCard: Card | null;
  activeColumn: TaskList | null;
  sensors: any;
  collisionDetectionStrategy: CollisionDetection;
  handleDragStart: (event: DragStartEvent) => void;
  handleDragOver: (event: DragOverEvent) => void;
  handleDragEnd: (event: DragEndEvent) => Promise<void>;
}

export const useBoardDragAndDrop = (
  board: BoardDetails | null,
  setBoard: React.Dispatch<React.SetStateAction<BoardDetails | null>>,
  sendCardMove: (payload: CardMovePayload) => void
): UseBoardDragAndDropReturn => {

  const [activeCard, setActiveCard] = useState<Card | null>(null);
  const [activeColumn, setActiveColumn] = useState<TaskList | null>(null);

  const sensors = useSensors(
    useSensor(PointerSensor, {
      activationConstraint: { distance: 10 },
    })
  );

  const findContainer = (id: number | string): TaskList | undefined => {
    if (!board) return undefined;

    if (typeof id === 'string' && id.includes('list-')) {
      const listId = parseId(id);
      return board.lists.find((l) => l.id === listId);
    }

    const targetCardId = parseId(id);
    return board.lists.find((list) =>
      (list.cards || []).some((c) => c.id === targetCardId)
    );
  }

  const handleDragStart = (event: DragStartEvent) => {
    const { active } = event;
    if (active.data.current?.type === "Column") {
      setActiveColumn(active.data.current.list);
      return;
    }
    if (active.data.current?.type === "Card") {
      setActiveCard(active.data.current.card);
    }
  }

  const handleDragOver = (event: DragOverEvent) => {
    const { active, over } = event;
    if (!over || !board) return;
    if (active.data.current?.type === "Column") return;

    const activeIdString = active.id as string;
    const overIdString = over.id as string;

    if (activeIdString === overIdString) return;

    const activeContainer = findContainer(activeIdString);
    const overContainer = findContainer(overIdString);

    if (!activeContainer || !overContainer) return;

    setTimeout(() => {
      setBoard((prev) => {
        if (!prev) return null;

        const activeList = prev.lists.find(l => l.id === activeContainer.id);
        const overList = prev.lists.find(l => l.id === overContainer.id);

        if (!activeList || !overList) return prev;

        const activeItems = (activeContainer.cards || []).filter(c => c);
        const overItems = (overContainer.cards || []).filter(c => c);

        const activeIndex = activeItems.findIndex((i) => i.id === parseId(activeIdString));

        let overIndex;
        if (overIdString.includes('card-')) {
          overIndex = overItems.findIndex(c => c.id === parseId(overIdString));
          const isBelowOverItem =
            over &&
            active.rect.current.translated &&
            active.rect.current.translated.top > over.rect.top + over.rect.height;

          const modifier = isBelowOverItem ? 1 : 0;
          overIndex = overIndex >= 0 ? overIndex + modifier : overItems.length + 1;
        } else {
          overIndex = overItems.length + 1;
        }

        if (activeContainer.id === overContainer.id) {
          if (activeIndex === overIndex) return prev;

          const targetIndex = overItems.findIndex(c => c.id === parseId(overIdString));
          const newState = {
            ...prev,
            lists: prev.lists.map(l => {
              if (l.id === activeContainer.id) {
                return { ...l, cards: arrayMove(activeItems, activeIndex, targetIndex) };
              }
              return l;
            })
          };
          return cleanBoardState(newState);
        } else {
          const isAlreadyInOverContainer = overList.cards.some(c => c.id === parseId(activeIdString));
          if (isAlreadyInOverContainer) return prev;

          let newIndex;
          if (overItems.some(c => c.id === parseId(overIdString))) {
            newIndex = overIndex >= 0 ? overIndex + (active.rect.current.translated && active.rect.current.translated.top > over.rect.top + over.rect.height ? 1 : 0) : overItems.length;
          } else {
            newIndex = overItems.length;
          }

          const newState = {
            ...prev,
            lists: prev.lists.map((l) => {
              if (l.id === activeContainer.id) {
                return { ...l, cards: activeItems.filter((item) => item.id !== parseId(activeIdString)) };
              }
              if (l.id === overContainer.id) {
                const newCardState = { ...activeCard, taskListId: overContainer.id };
                const newCards = [...overItems];
                newCards.splice(newIndex, 0, newCardState as Card);
                return { ...l, cards: newCards };
              }
              return l;
            }),
          };
          return cleanBoardState(newState);
        }
      });
    }, 0)
  }

  const handleDragEnd = async (event: DragEndEvent) => {
    const { active, over } = event;

    setActiveCard(null);
    setActiveColumn(null);

    if (!over || !board) return;

    const activeIdString = active.id as string;
    const overIdString = over.id as string;

    // Moving a column
    if (active.data.current?.type === "Column") {
      if (activeIdString === overIdString) return;

      setBoard((prev) => {
        if (!prev) return null;
        const oldIndex = prev.lists.findIndex(l => `list-${l.id}` === activeIdString);
        const newIndex = prev.lists.findIndex(l => `list-${l.id}` === overIdString);
        const newLists = arrayMove(prev.lists, oldIndex, newIndex);

        const requests = newLists.map((list, index) => ({
          taskListId: list.id,
          newListOrder: index
        }));

        reorderTaskList(requests);
        return { ...prev, lists: newLists };
      })
      return;
    }

    // Moving a card
    const activeIdNum = parseId(activeIdString);
    const destList = board.lists.find(l => l.cards.some(c => c.id === activeIdNum));

    if (destList) {
      const destCards = destList.cards;
      const newIndex = destCards.findIndex(c => c.id === activeIdNum);

      const movePayload: CardMovePayload = {
        cardId: activeIdNum,
        sourceListId: 0,
        targetListId: destList.id,
        newPosition: newIndex
      }
      console.log("Enviando movimiento WS:", movePayload);
      sendCardMove(movePayload);
    }
  };

  const collisionDetectionStrategy: CollisionDetection = useCallback((args) => {
    const { active } = args;
    if (active.data.current?.type === "Column") {
      return rectIntersection(args);
    }
    return closestCorners(args);
  }, []);

  return {
    activeCard,
    activeColumn,
    sensors,
    collisionDetectionStrategy,
    handleDragStart,
    handleDragOver,
    handleDragEnd
  };
}
