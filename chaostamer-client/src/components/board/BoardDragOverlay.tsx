import { DragOverlay } from '@dnd-kit/core';
import TaskColumn from "./TaskColumn";
import SortableCard from "./SortableCard";
import type { Card, TaskList } from "../../types/board.types";

interface BoardDragOverlayProps {
  activeColumn: TaskList | null;
  activeCard: Card | null;
}

export function BoardDragOverlay({ activeColumn, activeCard }: BoardDragOverlayProps) {
  return (
    <DragOverlay>
      {activeColumn && (
        <div className="opacity-80 rotate-2 cursor-grabbing">
          <TaskColumn
            list={activeColumn}
            onCardAdded={() => { }}
            onUpdateTitle={async () => { }}
            onDeleteList={async () => { }}
            handleCardClick={() => { }}
          />
        </div>
      )}
      {activeCard && (
        <div className="opacity-80 rotate-2 cursor-grabbing">
          <SortableCard
            card={activeCard}
            onClick={() => { }}
          />
        </div>
      )}
    </DragOverlay>
  );
}
