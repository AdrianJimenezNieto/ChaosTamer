import { useEffect, useState } from "react";
import { useBoardStore } from "../store/boardStore";
import { getMyBoards, createBoard } from "../services/boardService";
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
          boards.length > 0 ?(
            boards.map((board) => (
              <Link key={board.id} to={`/board/${board.id}`}>
                <div
                  className="rounded-lg bg-gray-800 p-4 shadow.lg transition duration-200 hover:scale-105 hover:cursor-pointer"
                >
                  <h3 className="font-bold">{board.title}</h3>
                  {/* TODO: make this a link to /board/{board.id} */}
                </div>
              </Link>
            ))
          ) : (
            <p className="text-gray-400">No tienes tableros. ¡Crea uno!</p>
          )
        }
      </div>
    </div>
  );
}