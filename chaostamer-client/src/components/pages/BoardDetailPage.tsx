import { Link, useParams } from "react-router-dom";
import { useState, useEffect, useRef, useCallback } from "react";
import { getBoardDetails, createTaskList, reorderCardsPersistence, updateCard, deleteCard, updateTaskList, deleteTaskList, updateBoard, reorderTaskList } from "../../services/boardService";
import type { BoardDetails, TaskList, Card, ReorderCardRequest } from "../../models";
import TaskColumn from "../board/TaskColumn";
import { CardDetailModal } from "../board/CardDetailModal";
import { AppLayout } from "../layouts/AppLayout";
import { useBoardWebSocket } from "../../hooks/useBoardWebSocket";
import type { CardMovePayload } from "../../hooks/useBoardWebSocket"

// --- IMPORTS DND-KIT ---
import {
  DndContext,
  DragOverlay,
  PointerSensor,
  useSensor,
  useSensors,
  closestCorners,
  rectIntersection
} from '@dnd-kit/core';
import type { DragStartEvent, DragEndEvent, DragOverEvent, CollisionDetection } from "@dnd-kit/core";
import SortableCard from '../board/SortableCard';
import { arrayMove, horizontalListSortingStrategy, SortableContext } from "@dnd-kit/sortable";
import { LoadingSpinner } from "../ui/LoadingSpinner";
// ----------------------


export default function BoardDetailPage() {
  // Get the param from the URL
  const { boardId } = useParams<{ boardId: string }>();

  // Local states to handle the data, load and errors
  const [board, setBoard] = useState<BoardDetails | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [newListTitle, setNewListTitle] = useState('');
  const [selectedCard, setSelectedCard] = useState<Card | null>(null);
  // MODAL STATE
  const [isModalOpen, setIsModalOpen] = useState(false);
  // EDIT TITLE STATES
  const [isEditingTitle, setIsEditingTitle] = useState(false);
  const [titleInput, setTitleInput] = useState('');
  const titleInputRef = useRef<HTMLInputElement>(null);

  // Sync title when board loads
  useEffect(() => {
    if (board) {
      setTitleInput(board.title);
    }
  }, [board])

  // Focus the input when editing
  useEffect(() => {
    if (isEditingTitle && titleInputRef.current) {
      titleInputRef.current.focus();
      titleInputRef.current.select();
    }
  }, [isEditingTitle])

  // Filter to change board state
  const cleanBoardState = (board: BoardDetails): BoardDetails => {
    // Defensive check for null or undefined board/lists
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

  // ------------ DND-KIT CONFIG ---------------------
  const [activeCard, setActiveCard] = useState<Card | null>(null);
  const [activeColumn, setActiveColumn] = useState<TaskList | null>(null);

  const sensors = useSensors(
    useSensor(PointerSensor, {
      activationConstraint: { distance: 10 },
    })
  )
  // -------------------

  // DND-FUNCTIONS -----

  // Helper functions

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

  const parseId = (id: string | number): number => {
    if (typeof id === 'number') return id;
    return Number(id.replace('list-', '').replace('card-', ''));
  }

  // DND MANAGE DRAGGING

  const handleDragStart = (event: DragStartEvent) => {
    const { active } = event;

    // If it is a column
    if (active.data.current?.type === "Column") {
      setActiveColumn(active.data.current.list);
      return;
    }

    // If it is a card
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

    
    if( activeIdString === overIdString ) return;
    
    // Container detection
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
          // Over a card
          overIndex = overItems.findIndex(c => c.id === parseId(overIdString));
          const isBelowOverItem =
            over &&
            active.rect.current.translated &&
            active.rect.current.translated.top > over.rect.top + over.rect.height;

          const modifier = isBelowOverItem ? 1 : 0;
          overIndex = overIndex >= 0 ? overIndex + modifier : overItems.length + 1;
        } else {
            // If we are over an empty column (overId is 'list-X')
            overIndex = overItems.length + 1;
        }

        if (activeContainer.id === overContainer.id) {
          // Reorder on the same list
          if (activeIndex === overIndex) return prev; // No changes

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
        }

        // Move throught lists
        else {
          // Verify is the card is already on the list
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

    // Clean the state
    setActiveCard(null);
    setActiveColumn(null);

    // Do nothing if we dont drop
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

        // Reorder in memory
        const newLists = arrayMove(prev.lists, oldIndex, newIndex);

        // Data for the backend 
        const requests = newLists.map((list, index) => ({
          taskListId: list.id,
          newListOrder: index
        }));

        // Call the api
        reorderTaskList(requests);

        return { ...prev, lists: newLists };
      })
      return;
    }

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

    // State Cleaning
    setActiveCard(null);
    setActiveColumn(null);

    // ------------------------ PERSISTENCE LOGIC --------------------------------

    // const updates: ReorderCardRequest[] = [];

    // // For each list of a board
    // board.lists.forEach((list) => {
    //   // For each card of a list 
    //   (list.cards || []).forEach((card, index) => { // Use the index as the order
    //     if (card && card.id) {
    //       updates.push({
    //         cardId: card.id,
    //         newTaskListId: list.id,
    //         newCardOrder: index
    //       });
    //     }
    //   });
    // });

    // // Call the API if there is something to save
    // if (updates.length > 0) {
    //   try {
    //     // Send the full picture of the cards
    //     console.log("💾 Guardando el nuevo orden...", updates);
    //     await reorderCardsPersistence(updates);
    //     console.log("✅ Orden guardado correctamente.");
    //   } catch (error) {
    //     console.error("❌ Error al guardar el orden:", error);
    //     // TODO: createa toast or something to log the errors to the user
    //     setError("Error de conexión: No se guardó el último movimiento.");
    //   }
    // }
  };

  // ------------------------ WEBSOCKET HANDLER --------------------------------
  const handleWebSocketEvent = (event: any) => {
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
        // Find the card on the board and delete it
        let movedCard: Card | undefined;
        const newLists = prev.lists.map(list => {
          const card = list.cards.find(c => c.id === payload.cardId);
          if (card) {
            movedCard = {... card, taskListId: payload.targetListId}; // Clone the card
            return { ...list, cards: list.cards.filter(c => c.id !== payload.cardId)};
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
              return { ...list, cards: newCards};
            }
            return list;
          })
        };
      });
    }
  };

  // Initialice Hook
  const { sendCardMove } = useBoardWebSocket(boardId, handleWebSocketEvent);

  // ------------------- EDIT TITLE HANDLER -------------------------------
  const handleUpdateBoardTitle = async () => {
    if (!board) return;

    if (!titleInput.trim() || titleInput === board.title) {
      setIsEditingTitle(false);
      setTitleInput(board.title);
      return;
    }

    const oldTitle = board.title;

    setBoard({ ...board, title: titleInput });
    setIsEditingTitle(false);

    try {
      await updateBoard(board.id, { title: titleInput });
    } catch (error) {
      setBoard({ ...board, title: oldTitle });
      setTitleInput(oldTitle);
      console.error("Error al actualizar el titulo: ", error)
    }
  }

  const handleTitleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === "Enter") handleUpdateBoardTitle();
    if (e.key === "Escape") {
      if (board) setTitleInput(board.title);
      setIsEditingTitle(false);
    }
  }

  // useEffect hook: runs when the component is mounted
  useEffect(() => {
    // runs only if we have a boardId
    if (!boardId) {
      setError('No se ha proposrcionado un ID de tablero');
      setIsLoading(false);
      return;
    }

    const fetchBoard = async () => {
      try {
        setIsLoading(true);
        // Call the API service
        const data = await getBoardDetails(boardId);
        setBoard(data);
        setError(null);
      } catch (err) {
        if (err instanceof Error) setError(err.message);
      } finally {
        setIsLoading(false);
      }
    };

    fetchBoard();
  }, [boardId]); // It reruns if the URL changes

  // New List handler
  const handleCreateList = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    if (newListTitle.trim() === '' || !boardId) return;

    try {
      // Call the API
      const newList = await createTaskList(boardId, newListTitle);

      // Update the local state
      if (board) {
        setBoard({
          ...board,
          lists: [...board.lists, newList]
        });
      }

      // Clean the input
      setNewListTitle('');
    } catch (err) {
      if (err instanceof Error) setError(err.message);
    }
  };

  // Function to handle the columns
  const handleCardAdded = (listId: number, newCard: Card) => {
    if (!board) return;

    // Update the inmutable state
    const updateLists = board.lists.map((list) => {
      if (list.id === listId) {
        // If it is the correct list, add the list
        return { ...list, cards: [...list.cards, newCard] }
      }
      return list;
    });

    setBoard({ ...board, lists: updateLists })
  };

  // MODAL HANDLERS
  // Open modal
  const handleCardClick = (card: Card) => {
    setSelectedCard(card);
    setIsModalOpen(true);
  };

  // Close modal
  const handleCloseModal = () => {
    setSelectedCard(null);
    setIsModalOpen(false);
  };

  // Save changes (API + Local state)
  const handleSaveCardDetails = async (cardId: number, newTitle: string, newDescription: string) => {
    if (!board) return;

    // Call the API
    const updatedCard = await updateCard(cardId, { title: newTitle, description: newDescription });
    console.log("✅ Tarjeta actualizada correctamente.");
    // Update the local state
    const newState = { ...board };
    newState.lists = newState.lists.map(list => ({
      ...list,
      cards: list.cards.map(card =>
        card.id === cardId ? updatedCard : card // replace the old card with the new one
      )
    }));

    setBoard(cleanBoardState(newState));
  };

  // Delete function
  const handleDeleteCard = async (cardId: number) => {
    if (!board) return;

    // Call the API
    await deleteCard(cardId);

    // Update the state
    const newState = { ...board };
    newState.lists = newState.lists.map(list => ({
      ...list,
      cards: list.cards.filter(c => c.id !== cardId)
    }));

    setBoard(cleanBoardState(newState));
  }

  // ------------------ UPDATE TASKLIST TITLE HANDLERS -----------------------
  const handleUpdateListTitle = async (listId: number, newTitle: string) => {
    if (!board) return;

    const oldBoard = { ...board }; // save a copy if it fails

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
      // call the api
      await updateTaskList(listId, { title: newTitle });
    } catch (error) {
      console.error("No se pudo actualizar el título: ", error);
      // set the old board
      setBoard(cleanBoardState(oldBoard));
    }
  }

  // ------------------------ DELETE TASKLIST HANDLERS ----------------------
  const handleDeleteList = async (listId: number) => {
    if (!board) return;

    // Save the prev board
    const oldBoard = { ...board };

    // Optimistic update
    setBoard(prev => {
      if (!prev) return null;
      return {
        ...prev,
        lists: prev.lists.filter(l => l.id !== listId)  // Filter the list
      }
    })

    try {
      // Call the service
      await deleteTaskList(listId);
    } catch (error) {
      console.error("Error al eliminar la lista: ", error);
      setBoard(cleanBoardState(oldBoard));
      throw new Error("Hubo un error al eliminar la lista")
    }
  };

  // Custom collision algorithm
  const collisionDetectionStrategy: CollisionDetection = useCallback((args) => {
    const { active } = args;

    // If we are moving a COLUMN
    if (active.data.current?.type === "Column") {
      return rectIntersection(args);
    }

    // If it is a card use closestcorners
    return closestCorners(args);
  }, []);

  // Board render
  return (
    <AppLayout>
      {isLoading ? (
        <LoadingSpinner size="lg" centered/>
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
            <div className="flex-1 mb-3">
              {isEditingTitle ? (
                <input
                  ref={titleInputRef}
                  type="text"
                  value={titleInput}
                  onChange={(e) => setTitleInput(e.target.value)}
                  onBlur={handleUpdateBoardTitle}
                  onKeyDown={handleTitleKeyDown}
                  className="text-3xl font-bold text-white bg-transparent w-full max-w-lg px-2 py-1 focus:outline-none focus:ring-2 focus:ring-blue-500 bg-gray-700"
                />
              ) : (
                <h1
                  onClick={() => setIsEditingTitle(true)}
                  className="text-3xl font-bold text-white cursor-pointer px-2 py-1 transition-colors inline-block border border-transparent"
                  title="Haz clic para editar el título"
                >
                  {board?.title}
                </h1>
              )}
            </div>

            {/* Horizontal container for the lists */}
            <div className="flex h-full gap-4 overflow-x-auto overflow-y-hidden pb-4">
              <SortableContext
                items={board?.lists.map(l => `list-${l.id}`) || []}
                strategy={horizontalListSortingStrategy}
              >
                {/* Map the lists */}
                {board.lists.length > 0 ? (
                  board.lists.map((list: TaskList) => (
                    <TaskColumn
                      key={list.id}
                      list={list}
                      onCardAdded={handleCardAdded}
                      handleCardClick={handleCardClick}
                      onUpdateTitle={handleUpdateListTitle}
                      onDeleteList={handleDeleteList}
                    />
                  ))
                ) : (
                  <p className="text-sm text-gray-400">Este tablero aún no tiene listas.</p>
                )}
              </SortableContext>

              {/* Form to add new list (US-202) */}
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
            {/* Overlay to show the card while its moving */}
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
                  <SortableCard card={activeCard} onClick={() => { }} />
                </div>
              )}
            </DragOverlay>
            <CardDetailModal
              isOpen={isModalOpen}
              onClose={handleCloseModal}
              card={selectedCard}
              onSave={handleSaveCardDetails}
              onDelete={handleDeleteCard}
            />
          </div>
        </DndContext>
      )}
    </AppLayout>
  )
}