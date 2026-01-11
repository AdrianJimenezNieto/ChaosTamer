import { useState } from "react";
import { SortableContext, horizontalListSortingStrategy } from "@dnd-kit/sortable";

import TaskColumn from "./TaskColumn";
import type { BoardDetails, Card, TaskList } from "../../types/board.types";

interface BoardCanvasProps {
  board: BoardDetails | null;
  createList: (title: string, boardId: string) => Promise<void>;
  onCardAdded: (listId: number, newCard: Card) => void;
  handleCardClick: (card: Card) => void;
  updateListTitle: (listId: number, newTitle: string) => Promise<void>;
  deleteList: (listId: number) => Promise<void>;
}

export function BoardCanvas({
  board,
  createList,
  onCardAdded,
  handleCardClick,
  updateListTitle,
  deleteList
}: BoardCanvasProps) {

  const [newListTitle, setNewListTitle] = useState('');

  const handleCreateList = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    if (!board) return;
    createList(newListTitle, String(board.id));
    setNewListTitle('');
  };

  if (!board) return null;

  return (
    <div className="flex h-full gap-4 overflow-x-auto overflow-y-hidden pb-4">
      <SortableContext
        items={board.lists.map(l => `list-${l.id}`) || []}
        strategy={horizontalListSortingStrategy}
      >
        {board.lists.length > 0 ? (
          board.lists.map((list: TaskList) => (
            <TaskColumn
              key={list.id}
              list={list}
              onCardAdded={onCardAdded}
              handleCardClick={handleCardClick}
              onUpdateTitle={updateListTitle}
              onDeleteList={deleteList}
            />
          ))
        ) : (
          <p className="text-sm text-gray-400">Este tablero aún no tiene listas.</p>
        )}
      </SortableContext>

      {/* Form to add new list */}
      <div className="w-72 flex-shrink-0">
        <form onSubmit={handleCreateList} className="rounded-lg bg-gray-800 p-4">
          <input
            type="text"
            value={newListTitle}
            onChange={(e) => setNewListTitle(e.target.value)}
            placeholder="Añadir nueva lista..."
            className="w-full rounded-md border border-gray-600 bg-gray-700 px-3 py-2 text-white placeholder-gray-400 focus:border-blue-500 focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
          <button
            type="submit"
            className="mt-2 w-full rounded-md bg-blue-600 px-4 py-2 text-sm font-semibold text-white transition duration-200 ease-in-out hover:bg-blue-700"
          >
            Crear Lista
          </button>
        </form>
      </div>
    </div>
  );
}
