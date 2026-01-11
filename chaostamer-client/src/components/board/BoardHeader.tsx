import { useState, useRef, useEffect } from "react";
import type { BoardDetails } from "../../types/board.types";

interface BoardHeaderProps {
  board: BoardDetails | null;
  updateBoardTitle: (newTitle: string) => Promise<void>;
}

export function BoardHeader({ board, updateBoardTitle }: BoardHeaderProps) {
  const [isEditingTitle, setIsEditingTitle] = useState(false);
  const [titleInput, setTitleInput] = useState('');
  const titleInputRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    if (board) {
      setTitleInput(board.title);
    }
  }, [board]);

  useEffect(() => {
    if (isEditingTitle && titleInputRef.current) {
      titleInputRef.current.focus();
      titleInputRef.current.select();
    }
  }, [isEditingTitle]);

  const handleBlur = () => {
    updateBoardTitle(titleInput);
    setIsEditingTitle(false);
  }

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === "Enter") handleBlur();
    if (e.key === "Escape") {
      if (board) setTitleInput(board.title);
      setIsEditingTitle(false);
    }
  }

  if (!board) return null;

  return (
    <div className="flex-1 mb-3">
      {isEditingTitle ? (
        <input
          ref={titleInputRef}
          type="text"
          value={titleInput}
          onChange={(e) => setTitleInput(e.target.value)}
          onBlur={handleBlur}
          onKeyDown={handleKeyDown}
          className="text-3xl font-bold text-white bg-transparent w-full max-w-lg px-2 py-1 focus:outline-none focus:ring-2 focus:ring-blue-500 bg-gray-700"
        />
      ) : (
        <h1
          onClick={() => setIsEditingTitle(true)}
          className="text-3xl font-bold text-white cursor-pointer px-2 py-1 transition-colors inline-block border border-transparent"
          title="Haz clic para editar el título"
        >
          {board.title}
        </h1>
      )}
    </div>
  );
}
