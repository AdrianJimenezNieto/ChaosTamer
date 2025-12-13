package com.taskflow.domain.service;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.taskflow.domain.model.Board;
import com.taskflow.domain.model.TaskList;
import com.taskflow.domain.model.User;

import com.taskflow.domain.port.in.CreateTaskListUseCase;
import com.taskflow.domain.port.in.DeleteTaskListUseCase;
import com.taskflow.domain.port.in.ReorderTaskListUseCase;
import com.taskflow.domain.port.in.UpdateTaskListUseCase;

import com.taskflow.domain.port.out.BoardRepositoryPort;
import com.taskflow.domain.port.out.TaskListRepositoryPort;
import com.taskflow.domain.port.out.UserRepositoryPort;

import com.taskflow.infrastructure.adapter.in.web.dto.UpdateTaskListRequest;
import com.taskflow.infrastructure.adapter.in.web.dto.ReorderTaskListRequest;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskListService implements
    CreateTaskListUseCase,
    UpdateTaskListUseCase,
    DeleteTaskListUseCase,
    ReorderTaskListUseCase{
    
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
        List<TaskList> existingLists = taskListRepositoryPort.findAllByBoardId(board.getId());
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

    // Reorder Lists
    @Override
    @Transactional
    public void reorderTaskLists(List<ReorderTaskListRequest> requests, String username) {
        if (requests.isEmpty()) return;

        // Get the id of a list 
        Long sampleListId = requests.get(0).getTaskListId();
        TaskList sampleList = taskListRepositoryPort.findById(sampleListId)
            .orElseThrow(() -> new EntityNotFoundException("Lista no encontrada"));

        // SECURITY
        Board board = boardRepositoryPort.findById(sampleList.getBoardId())
            .orElseThrow(() -> new EntityNotFoundException("Tablero no encontrado"));
        User user = userRepositoryPort.findByEmail(username)
            .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        if (!board.getUserId().equals(user.getId())) {
            throw new AccessDeniedException("No tienes permiso para editar este tablero");
        }

        // Map the changes
        Map<Long, Integer> changes = requests.stream()
            .collect(Collectors.toMap(ReorderTaskListRequest::getTaskListId, ReorderTaskListRequest::getNewListOrder));

        
        // All the lists in the board
        List<TaskList> allLists = taskListRepositoryPort.findAllByBoardId(board.getId());


        // 5. Aplicar cambios en memoria
        for (TaskList list : allLists) {
            if (changes.containsKey(list.getId())) {
                list.setListOrder(changes.get(list.getId()));
            }
        }



        Set<Long> movedLists = requests.stream()
            .map(ReorderTaskListRequest::getTaskListId)
            .collect(Collectors.toSet());


        // Tie breaker
        reindexBoard(allLists, movedLists);
    }

    private void reindexBoard(List<TaskList> lists, Set<Long> movedListIds) {
        // Controller of the comparator
        HashSet<Long> alreadyCompared = new HashSet<>();
        // Sort them using the comparator
        Comparator<TaskList> comparator = (TaskList l1, TaskList l2) -> {
            // Intended order
            int orderComparison = l1.getListOrder().compareTo(l2.getListOrder());

            if (orderComparison != 0) {
                return orderComparison;
            }

            // Tie breaker
            boolean l1WasMoved = movedListIds.contains(l1.getId());
            boolean l2WasMoved = movedListIds.contains(l2.getId());

            if ((l1WasMoved && !l2WasMoved) && !(alreadyCompared.contains(l1.getId()) && alreadyCompared.contains(l2.getId()))) {
                alreadyCompared.add(l1.getId());
                alreadyCompared.add(l2.getId());
                if (l1.getId() < l2.getId()){
                return 1;
                }
                return -1;
            }
            if ((!l1WasMoved && l2WasMoved) && !(alreadyCompared.contains(l1.getId()) && alreadyCompared.contains(l2.getId()))) {
                alreadyCompared.add(l1.getId());
                alreadyCompared.add(l2.getId());
                if (l2.getId() < l1.getId()){
                return -1;
                }
                return 1;
            }

            return l2.getId().compareTo(l1.getId());
        };

        lists.sort(comparator);

        for (int i = 0; i < lists.size(); i++) {
            TaskList list = lists.get(i);
            if (!list.getListOrder().equals(i)) {
                list.setListOrder(i);
            }
            taskListRepositoryPort.save(list);
        }
    }
}
