import { useEffect, useRef, useState } from "react";
import { useBoardStore } from "../store/boardStore";
import { getMyBoards, createBoard, updateBoard, deleteBoard } from "../services/boardService";
import { Link } from "react-router-dom";

export default function DashboardPage() {

  // Connect the Zustand Store
  const boards = useBoardStore((state) => state.boards);
  const setBoards = useBoardStore((state) => state.setBoards);
  const addBoard = useBoardStore((state) => state.addBoard);

  // Local state for the form and the load
  const [newBoardTitle, setNewBoardTitle] = useState('');
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // EDITING BOARD STATES
  const [activeMenuId, setActiveMenuId] = useState<number | null>(null);
  const [editingBoardId, setEditingBoardId] = useState<number | null>(null);
  const [tempTitle, setTempTitle] = useState('');

  const inputRef = useRef<HTMLInputElement>(null);

  // Effect for loading the boards (US-106)
  // It runs only 1 time when the components is built
  useEffect(() => {
    const fetchBoards = async () => {
      try {
        setIsLoading(true);
        const fetchedBoards = await getMyBoards();
        // Save the boards into the global store
        setBoards(fetchedBoards);
        setError(null); // Delete any previous error
      } catch (error) {
        if (error instanceof Error) setError(error.message);
      } finally {
        setIsLoading(false);
      }
    };

    fetchBoards();
  }, [setBoards]);

  // EDIT BOARD HANLDERS
  useEffect(() => {
    const handleClickOutside = () => setActiveMenuId(null);
    if (activeMenuId !== null) {
      window.addEventListener('click', handleClickOutside)
    }
    return () => window.removeEventListener('click', handleClickOutside);
  }, [activeMenuId]);

  useEffect(() => {
    if (editingBoardId !== null && inputRef.current) {
      inputRef.current.focus();
      inputRef.current.select();
    }
  }, [editingBoardId]);

  const toggleMenu = (e: React.MouseEvent, boardId: number) => {
    e.preventDefault();
    e.stopPropagation();
    setActiveMenuId(activeMenuId === boardId ? null : boardId);
  }

  const startEditing = (e: React.MouseEvent, boardId: number, currentTitle: string) => {
    e.preventDefault(); // ¡Importante!
    e.stopPropagation();
    setActiveMenuId(null); // Cerrar menú
    setEditingBoardId(boardId);
    setTempTitle(currentTitle);
  };
  

  const handleUpdateBoard = async () => {
    if(editingBoardId === null) return;

    const board = boards.find(b => b.id === editingBoardId);
    if(!tempTitle.trim() || (board && board.title === tempTitle)) {
      setEditingBoardId(null);
      return;
    }
    try {
      // Call the API
      const updatedBoard = await updateBoard(editingBoardId, { title: tempTitle });

      const updatedBoards = boards.map(b => b.id === editingBoardId ? updatedBoard : b);
      setBoards(updatedBoards);

      // Close the modal
      setEditingBoardId(null);
    } catch (error) {
      console.error("Error al actualizar el tablero: ", error);
    }
  }

  const handleDeleteBoard = async (e: React.MouseEvent, boardId: number) => {
    e.preventDefault();
    e.stopPropagation();

    const boardToDelete = boards.find(b => b.id === boardId);
    const confirmMessage = boardToDelete
      ? `¿Estás seguro de que quieres eliminar el tablero ${boardToDelete.title} y todo lo que contiene?\n\nEsta acción no puede revertirse`
      : "¿Estás seguro de eliminar este tablero?";

    if (window.confirm(confirmMessage)) {
      try {
        // call the service
        await deleteBoard(boardId);

        // Update local state Zustand
        const newBoards = boards.filter(b => b.id !== boardId);
        setBoards(newBoards);

        setActiveMenuId(null); // close the menu
      } catch (error) {
        console.error("No se ha podido eliminar el tablero: ", error);
        // TODO: toast notification
      }
    }
  }

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter') {
        e.preventDefault(); // Evitar submit de form si lo hubiera
        handleUpdateBoard();
    }
    if (e.key === 'Escape') setEditingBoardId(null); // Cancelar
  };

  // Function to hanlde the board creation
  const handleCreateBoard = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    if (newBoardTitle.trim() === '') return; // Skip empty titles

    try {
      const newBoard = await createBoard(newBoardTitle);
      // add the new board into the store
      addBoard(newBoard);
      // clean the input
      setNewBoardTitle('');
    } catch (error) {
      if (error instanceof Error) setError(error.message);
    }
  };

  // Evitar que hacer clic en el input navegue al tablero
  const handleInputClick = (e: React.MouseEvent) => {
    e.preventDefault();
    e.stopPropagation();
  };

  // Rendering
  if (isLoading) {
    return (
      <div className="text-white">Cargando tableros...</div>
    )
  }

  return (
    <div className="text-white p-5">
      <h1 className="mb-6 text-3xl font-bold">Mis Tableros</h1>

      {/* Creation Form */}
      <form onSubmit={handleCreateBoard} className="mb-6 flex gap-2">
        <input 
          type="text"
          value= {newBoardTitle}
          onChange={(e) => setNewBoardTitle(e.target.value)}
          placeholder="Título del nuevo tablero"
          className="flex-grow rounded-md border border-gray-600 bg-gray-700 px-3 py-2 text-white placerholder-gray-400 focus:border-blue-500 focus:outline-none focus:ring-2 focus:ring-blue-500"
        />
        <button
          type="submit"
          className="rounded-md bg-blue-600 px-4 py-2 font-semibold text-white transition duration-200 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500"
        >
          Crear
        </button>
      </form>

      {/* Error Message */}
      {error && <p className="mb-4 text-red-400">{error}</p>}

      {/* Board List */}
      <div className="grid grid-cols-1 gap-4 md:grid-cols-3 lg:grid-cols-4">
        {
          boards.length > 0 ? (
            boards.map((board) => (
              <div key={board.id} className='relative group'>
                <Link to={`/board/${board.id}`}>
                  <div
                    className="rounded-lg bg-gray-800 p-4 shadow.lg transition duration-200 cursor-pointer"
                  >
                    <div className="flex-1 min-w-0 pr-8">
                      {editingBoardId === board.id ? (
                        <input
                          ref={inputRef}
                          type="text"
                          value={tempTitle}
                          onChange={(e) => setTempTitle(e.target.value)}
                          onBlur={handleUpdateBoard}
                          onKeyDown={handleKeyDown}
                          onClick={handleInputClick}
                          className="w-full bg-gray-700 text-white border border-blue-500 rounded px-2 py-1 focus:outline-none"
                        />
                      ) : (
                        <h3 className="font-bold text-lg truncate text-white" title={board.title}>
                          {board.title}
                        </h3>
                      )}
                    </div>
                    {/* KEBAB MENU */}
                    <button
                      onClick={(e) => toggleMenu(e, board.id)}
                      className="absolute top-3 right-3 p-1 text-gray-400 hover:text-white hover:bg-gray-600 rounded-full transition-all z-10"
                    >
                      <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 5v.01M12 12v.01M12 19v.01M12 6a1 1 0 110-2 1 1 0 010 2zm0 7a1 1 0 110-2 1 1 0 010 2zm0 7a1 1 0 110-2 1 1 0 010 2z" />
                      </svg>
                    </button>
                  </div>
                </Link>

                {/* DROPDOWN MENU */}
                {activeMenuId === board.id && (  
                  <div
                    className="absolute top-10 right-2 w-40 bg-white rounded shadow-xl z-20 py-1 overflow-hidden animate-in fade-in zoom-in duration-100"
                    onClick={(e) => e.stopPropagation()}
                  >
                    <button
                      onClick={(e) => startEditing(e, board.id, board.title)}
                      className="w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 flex items-center gap-2"
                    >
                      <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z" /></svg>
                      Renombrar
                    </button>
                    <button
                      onClick={(e) => handleDeleteBoard(e, board.id)}
                      className="w-full text-left px-4 py-2 text-sm text-red-600 hover:bg-red-50 flex items-center gap-2"
                    >
                      <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" /></svg>
                      Eliminar
                    </button>
                    </div>
                )}
              </div>
            ))
          ) : (
            <p className="text-gray-400">No tienes tableros. ¡Crea uno!</p>
          )
        }
      </div>
    </div>
  );
}