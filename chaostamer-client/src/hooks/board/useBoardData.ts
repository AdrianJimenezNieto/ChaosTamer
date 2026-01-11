import { useState, useEffect, useCallback } from "react";
import { getBoardDetails } from "../../services/boardService";
import type { BoardDetails } from "../../types/board.types";

export interface UseBoardDataReturn {
  board: BoardDetails | null;
  setBoard: React.Dispatch<React.SetStateAction<BoardDetails | null>>;
  isLoading: boolean;
  error: string | null;
  refreshBoard: () => Promise<void>;
}

export const useBoardData = (boardId: string | undefined): UseBoardDataReturn => {
  // 1. Local State Definition
  const [board, setBoard] = useState<BoardDetails | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // 2. Data Fetching Function
  const fetchBoard = useCallback(async () => {
    if (!boardId) {
      setError('No se ha proporcionado un ID de tablero');
      setIsLoading(false);
      return;
    }

    try {
      setIsLoading(true); // Start loading
      const data = await getBoardDetails(boardId); // API Call
      setBoard(data); // Update state with data
      setError(null); // Clear errors
    } catch (err) {
      // Error handling
      if (err instanceof Error) setError(err.message);
    } finally {
      setIsLoading(false); // Stop loading
    }
  }, [boardId]);

  // 3. Effect to trigger fetch on mount or id change
  useEffect(() => {
    fetchBoard();
  }, [fetchBoard]);

  // 4. Return values for the component to use
  return { board, setBoard, isLoading, error, refreshBoard: fetchBoard };
}
