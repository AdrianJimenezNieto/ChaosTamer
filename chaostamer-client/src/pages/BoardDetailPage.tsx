import { useState } from "react";
import { Link, useParams } from "react-router-dom";
import { DndContext } from '@dnd-kit/core';

import { AppLayout } from "../components/layouts/AppLayout";
import { LoadingSpinner } from '../components/ui/LoadingSpinner';

// Board Components
import {
  BoardHeader,
  BoardCanvas,
  BoardDragOverlay,
  CardDetailModal
} from "../components/board";

// Board Hooks
import {
  useBoardData,
  useBoardOperations,
  useBoardDragAndDrop,
  useBoardRealtime
} from "../hooks/board";

import type { Card } from "../types/board.types";

export default function BoardDetailPage() {
  const { boardId } = useParams<{ boardId: string }>();

  // 1. Data & State Management
  const { board, setBoard, isLoading, error } = useBoardData(boardId);
  const {
    createList,
    deleteList,
    updateListTitle,
    updateBoardTitle,
    saveCard,
    deleteCardOp,
    onCardAdded
  } = useBoardOperations(board, setBoard);

  // 2. Realtime Updates
  const { sendCardMove } = useBoardRealtime(boardId, setBoard);

  // 3. Drag and Drop Logic
  const {
    activeCard,
    activeColumn,
    sensors,
    collisionDetectionStrategy,
    handleDragStart,
    handleDragOver,
    handleDragEnd
  } = useBoardDragAndDrop(board, setBoard, sendCardMove);

  // 4. Modal State (Kept local as it is UI state)
  const [selectedCard, setSelectedCard] = useState<Card | null>(null);
  const [isModalOpen, setIsModalOpen] = useState(false);

  const handleCardClick = (card: Card) => {
    setSelectedCard(card);
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    setSelectedCard(null);
    setIsModalOpen(false);
  };

  // Render
  return (
    <AppLayout>
      {isLoading ? (
        <LoadingSpinner size="lg" centered />
      ) : error ? (
        <div className="flex flex-col items-center justify-center h-[60vh] text-center">
          <div className="bg-red-500/10 p-6 rounded-lg border border-red-500/50 max-w-md">
            <h3 className="text-red-500 text-xl font-bold mb-2">Ocurrió un error</h3>
            <p className="text-gray-300">{error}</p>
            <button
              onClick={() => window.location.reload()}
              className="mt-4 text-sm text-red-400 hover:text-red-300 underline"
            >
              Reintentar
            </button>
          </div>
        </div>
      ) : !board ? (
        <div className="flex flex-col items-center justify-center h-[60vh]">
          <p className="text-gray-400 text-xl">No hemos encontrado este tablero.</p>
          <Link to="/dashboard" className="mt-4 text-indigo-400 hover:text-indigo-300">
            &larr; Volver al Dashboard
          </Link>
        </div>
      ) : (
        <DndContext
          sensors={sensors}
          collisionDetection={collisionDetectionStrategy}
          onDragStart={handleDragStart}
          onDragOver={handleDragOver}
          onDragEnd={handleDragEnd}
        >
          <div className="text-white h-full flex flex-col p-4">

            <BoardHeader
              board={board}
              updateBoardTitle={updateBoardTitle}
            />

            <BoardCanvas
              board={board}
              createList={createList}
              onCardAdded={onCardAdded}
              handleCardClick={handleCardClick}
              updateListTitle={updateListTitle}
              deleteList={deleteList}
            />

            <BoardDragOverlay
              activeColumn={activeColumn}
              activeCard={activeCard}
            />

            <CardDetailModal
              isOpen={isModalOpen}
              onClose={handleCloseModal}
              card={selectedCard}
              onSave={saveCard}
              onDelete={deleteCardOp}
            />
          </div>
        </DndContext>
      )}
    </AppLayout>
  )
}