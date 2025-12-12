package com.taskflow.domain.service;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.taskflow.domain.model.Board;
import com.taskflow.domain.model.TaskList;
import com.taskflow.domain.model.User;
import com.taskflow.domain.port.in.CreateTaskListUseCase;
import com.taskflow.domain.port.in.DeleteTaskListUseCase;
import com.taskflow.domain.port.in.UpdateTaskListUseCase;

import com.taskflow.domain.port.out.BoardRepositoryPort;
import com.taskflow.domain.port.out.TaskListRepositoryPort;
import com.taskflow.domain.port.out.UserRepositoryPort;
import com.taskflow.infrastructure.adapter.in.web.dto.UpdateTaskListRequest;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaskListService implements
    CreateTaskListUseCase,
    UpdateTaskListUseCase,
    DeleteTaskListUseCase {
    
    private final TaskListRepositoryPort taskListRepositoryPort;
    private final BoardRepositoryPort boardRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;

    @Override
    public TaskList createTaskList(CreateTaskListCommand command, String ownerUsername) {
        // Find the user (sec check)
        User user = userRepositoryPort.findByEmail(ownerUsername)
        .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        // Find the board that belongs to
        Board board = boardRepositoryPort.findById(command.getBoardId())
        .orElseThrow(() -> new EntityNotFoundException("Tablero no encontrado"));

        // SECURITY CHECK
        if (!board.getUserId().equals(user.getId())) {
        throw new AccessDeniedException("No tienes permiso para añadir listas a este tablero");
        }

        // Initialize the listOrder
        List<TaskList> existingLists = taskListRepositoryPort.findAllByBoard(board.getId());
        int newOrder = existingLists.size();

        // Create the domain object
        TaskList newTaskList = TaskList.builder()
        .title(command.getTitle())
        .boardId(command.getBoardId())
        .listOrder(newOrder)
        .build();

        // Return using the persistence port
        return taskListRepositoryPort.save(newTaskList);
    }

    // US-205: Update tasklist
    @Override
    @Transactional
    public TaskList updateTaskList(Long taskListId, UpdateTaskListRequest request, String username) {
        // Get the tasklist
        TaskList taskList = taskListRepositoryPort.findById(taskListId)
        .orElseThrow(() -> new EntityNotFoundException("No se encontró la lista"));

        // Security
        Board board = boardRepositoryPort.findById(taskList.getBoardId())
        .orElseThrow(() -> new EntityNotFoundException("Tablero no encontrado"));
        User user = userRepositoryPort.findByEmail(username)
        .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        
        if (!board.getUserId().equals(user.getId())) {
        throw new AccessDeniedException("No tienes permiso para añadir listas a este tablero");
        }

        // Update
        taskList.setTitle(request.getTitle());

        // Persist changes and return
        return taskListRepositoryPort.save(taskList);
    }

    // US-208: Delete TaskList
    @Override
    @Transactional
    public void deleteTaskList(Long taskListId, String username) {
        // Get the list to veify security
        TaskList taskList = taskListRepositoryPort.findById(taskListId)
        .orElseThrow(() -> new EntityNotFoundException("Lista no encontrada"));
        Board board = boardRepositoryPort.findById(taskList.getBoardId())
        .orElseThrow(() -> new EntityNotFoundException("Tablero no encontrado"));

        User user = userRepositoryPort.findByEmail(username)
        .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        // Verify that the user is the owner of the list
        if (!board.getUserId().equals(user.getId())) {
        throw new AccessDeniedException("No tienes permiso para eliminar esta lista");
        }

        // Delete 
        taskListRepositoryPort.deleteById(taskListId);
    }
}
