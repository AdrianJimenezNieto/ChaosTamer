export type Board = {
  id: number;
  title: string; 
};

export type Card = {
  id: number;
  title: string;
  description: string | null;
  cardOrder: number;
  taskListId: number;
};

export type TaskList = {
  id: number;
  title: string;
  cards: Card[];
  listOrder: number;
};

export type BoardDetails = {
  id: number;
  title: string;
  lists: TaskList[];
};