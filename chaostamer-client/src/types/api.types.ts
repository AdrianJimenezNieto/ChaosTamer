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

// Type for reorder lists
export type ReorderTaskListRequest = {
  taskListId: number;
  newListOrder: number;
}