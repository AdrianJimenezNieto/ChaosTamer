
export type Board = {
  id: number;
  title: string; 
};

export type Card = {
  id: number;
  title: string;
  description: string | null;
  cardOrder: number;
};

export type TaskList = {
  id: number;
  title: string;
  cards: Card[];
};

export type BoardDetails = {
  id: number;
  title: string;
  lists: TaskList[];
};

// Type for the reorder request
export type ReorderCardRequest = {
  cardId: number;
  newTaskListId: number;
  newCardOrder: number;
};

// Type for updating the cards
export type UpdateCardRequest = {
  title?: string;
  description?: string;
};

// Type for updating tasklists
export type UpdateTaskListId = {
  title: string;
}

// Type for the board update
export type UpdateBoard = {
  title: string;
}