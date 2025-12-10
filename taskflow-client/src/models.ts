
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