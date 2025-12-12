import React, { useEffect, useRef, useState } from 'react';
import type { TaskList, Card } from '../../models';
import { createCard } from '../../services/boardService';
// DND-KIT IMPORTS
import { SortableContext, verticalListSortingStrategy } from '@dnd-kit/sortable';
import { useDroppable } from '@dnd-kit/core';
import SortableCard from './SortableCard';

interface TaskColumnProps {
  list: TaskList;
  // this property will advice the father when we create a new card
  onCardAdded: (listId: number, newCard: Card) => void;
  handleCardClick: (card: Card) => void;
  // function to update the title of a tasklist
  onUpdateTitle: (listId: number, newTitle: string) => Promise<void>;
  onDeleteList: (listId: number) => Promise<void>;  // Prop for deleting a taskList
}

export default function TaskColumn({ 
  list, 
  onCardAdded,
  handleCardClick, 
  onUpdateTitle,
  onDeleteList 
}: TaskColumnProps) {
  const [newCardTitle, setNewCardTitle] = useState('');
  // ---- EDITING STATES -----
  const [isEditing, setIsEditing] = useState(false);
  const [title, setTitle] = useState(list.title);
  const inputRef = useRef<HTMLInputElement>(null);

  const cardIds = (list.cards || [])
    .filter(c => c && c.id)
    .map((c) => String(c.id));
  
  // Define the droppable zone
  const { setNodeRef } = useDroppable({
    id: String(list.id),
    data: { type: 'list'},
  });

  const handleCreateCard = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!newCardTitle.trim()) return;

    try{
      // Call the api
      const newCard = await createCard(list.id, newCardTitle);
      // Advice the father to update the global state
      onCardAdded(list.id, newCard);
      // Clean the input
      setNewCardTitle('');
    } catch (error) {
      console.error(error);
      // TODO: manage local errors
    }
  };

  // -------- EDITING HANDLERS ---------
  useEffect(() => {
    setTitle(list.title);
  }, [list.title]);

  useEffect(() => {
    if (isEditing && inputRef.current) {
      inputRef.current.focus();
      inputRef.current.select();
    }
  }, [isEditing]);

  // Saving handler
  const handleSave = async () => {
    // if its empty or didnt change exit edit mode
    if (!title.trim() || title === list.title) {
      setTitle(list.title);
      setIsEditing(false);
      return;
    }

    try {
      await onUpdateTitle(list.id, title);
      setIsEditing(false);
    } catch (error) {
      // if it fails, go to the prev title
      setTitle(list.title);
      setIsEditing(false);
    }
  };

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter') handleSave();
    if (e.key === 'Escape') {
      setTitle(list.title);
      setIsEditing(false);
    }
  };

  // --------------------- DELETING HANDLERS -------------------------------
  const handleDeleteClick = () => {
    if (window.confirm(`¿Estás seguro que deseas eliminar la lista "${list.title}" y todas sus tarjetas? Esta acción no se puede revertir`)) {
      onDeleteList(list.id);
    }
  }

  return (
    <div className='w-72 flex-shrink-0'>
      <div className='rounded-lg bg-gray-800 shadow-xl p-4 flex flex-col h-full group'>

        {/* EDITABLE HEADER */}
        <div className='mb-4 font-bold text-gray-700 flex justify-between items-center gap-2'>
          {/* TITLE ZONE */}
          <div className='flex-1 min-w-0'>
          {
            isEditing ? (
              <input
                ref={inputRef} 
                value={title}
                onChange={(e) => setTitle(e.target.value)}
                onBlur={handleSave}
                onKeyDown={handleKeyDown}
                className='w-full rounded-md border border-gray-600 bg-gray-700 px-3 py-2 text-white placeholder-gray-400 focus:border-blue-500 focus:outline-none focus:ring-2 focus:ring-blue-500'
              />
            ) : (
              <h3
                onClick={() => setIsEditing(true)}
                title="Haz click para editar el nombre de la lista"
                className='text-xl px-3 py-2 text-white cursor-pointer rounded truncate border border-transparent'
              >
                {list.title}
              </h3>
            )
          }
          </div>

          {/* DELETE BUTTON ZONE (shown if not editing)*/}
          {!isEditing && (
            <button
              onClick={handleDeleteClick}
              className='text-gray-400 hover:text-red-600 p-1 rounded hover:bg-gray-200 transition-all duration-200 ease-in-out opacity-0 group-hover:opacity-100'
              title='Eliminar Lista'
            >
              {/* Trash Icon */}
              <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
              </svg>
            </button>
          )}
        </div>

        {/* List of Cards (with scroll if they are many) */}
        <div ref={setNodeRef} className='flex-1 flex flex-col gap-3 overflow-y-auto min-h-[50px] mb-3'>
          <SortableContext
            items={cardIds}
            strategy={verticalListSortingStrategy}
          >
            {list.cards && list.cards.length > 0 ? (
              list.cards.map((card) => (
                card ? <SortableCard key={card.id} card={card} onClick={() => handleCardClick(card)}/> : null
              ))
            ) : (
              <p className='text-sm text-gray-400'>No hay tarjetas.</p>
            )}
          </SortableContext>
        </div>

        {/* Form to add new card */}
        <form onSubmit={handleCreateCard} className='mt-auto'>
          <input
            type="text"
            value={newCardTitle}
            placeholder='Añadir nueva tarjeta...'
            onChange={(e) => setNewCardTitle(e.target.value)}
            className='w-full rounded bg-gray-900 px-2 py-1 text-sm text-white border border-gray-600 focus:border-blue-500 focus:outline-none'
          />
          <button
            type='submit'
            className='mt-2 w-full rounded bg-blue-600 px-2 py-1 text-xs font-bold text-white transition duration-200 ease-in-out hover:bg-blue-700'
          >
            Añadir
          </button>
        </form>
      </div>
    </div>
  )
}