import api from "../lib/axios";
import type { ReorderTaskListRequest, UpdateTaskListRequest } from "../types/api.types";
import type { TaskList } from "../types/board.types";

// US-202: Create new TaskList
export const createTaskList = async (boardId: string, title: string): Promise<TaskList> => {
  try{
    // Call the endpoint
    const response = await api.post<TaskList>(`/boards/${boardId}/lists`, { title });
    return response.data;
  } catch (error) {
    console.error("Error creating the task list: ", error);
    throw new Error('No se pudo crear la lista')
  }
};

// US-205: Update tasklist title
export const updateTaskList = async(taskListId: number, data: UpdateTaskListRequest): Promise<TaskList> => {
  try {
    // Call the api
    const response = await api.patch<TaskList>(`/tasklists/${taskListId}`, data);
    console.log("✅ Título de la lista actualizado correctamente")
    return response.data;
  } catch (error) {
    console.error("No se pudo editar el título de la lista", error);
    throw new Error("No se pudo editar el título de la lista")
  }
};

// US-208: Delete TaskList
export const deleteTaskList = async(taskListId: number): Promise<void> => {
  try {
    // Call the endpoint for deleting a TaskList
    await api.delete(`/tasklists/${taskListId}`);
    console.log("🗑️ Lista borrada correctamente.")
  } catch (error) {
    console.error("No se pudo eliminar la lista: ", error);
    throw new Error("No se ha podido eliminar la lista")
  }
};

// Reorder Lists
export const reorderTaskList = async (requests: ReorderTaskListRequest[]): Promise<void> => {
  try {
    await api.put(`tasklists/reorder`, requests);
  } catch (error) {
    console.error("Error reordenando las listas: ", error);
  }
}