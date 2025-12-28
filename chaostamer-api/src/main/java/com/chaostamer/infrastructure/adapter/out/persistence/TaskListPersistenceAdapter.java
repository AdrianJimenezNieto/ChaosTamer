package com.chaostamer.infrastructure.adapter.out.persistence;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import com.chaostamer.domain.model.TaskList;
import com.chaostamer.domain.port.out.TaskListRepositoryPort;
import com.chaostamer.infrastructure.adapter.out.persistence.entity.BoardEntity;
import com.chaostamer.infrastructure.adapter.out.persistence.entity.TaskListEntity;
import com.chaostamer.infrastructure.adapter.out.persistence.mapper.TaskListPersistenceMapper;
import com.chaostamer.infrastructure.adapter.out.persistence.repository.BoardJpaRepository;
import com.chaostamer.infrastructure.adapter.out.persistence.repository.TaskListJpaRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TaskListPersistenceAdapter implements TaskListRepositoryPort {
  
  private final TaskListJpaRepository taskListJpaRepository;
  private final BoardJpaRepository boardJpaRepository;
  private final TaskListPersistenceMapper taskListMapper;

  @Override
  public List<TaskList> findAllByBoardId(Long boardId) {
    return taskListJpaRepository.findAllByBoardId(boardId)
      .stream()
      .map(taskListMapper::toDomain)
      .collect(Collectors.toList());
  }

  @Override
  public TaskList save(TaskList taskList) {
    TaskListEntity taskListEntity;

    // UPDATE CASE
    if (taskList.getId() != null) {
      taskListEntity = taskListJpaRepository.findById(taskList.getId())
        .orElseThrow(() -> new EntityNotFoundException("Lista no encontrada"));
      
      // Update the list title
      taskListEntity.setTitle(taskList.getTitle());
      taskListEntity.setListOrder(taskList.getListOrder());
    } else {
      taskListEntity = taskListMapper.toEntity(taskList);

      // Owner assignment
      BoardEntity boardEntity = boardJpaRepository.findById(taskList.getBoardId())
        .orElseThrow(() -> new EntityNotFoundException("Tablero no encontrado"));
      taskListEntity.setBoard(boardEntity);
    }
    
    // Save
    TaskListEntity savedEntity = taskListJpaRepository.save(taskListEntity);
    return taskListMapper.toDomain(savedEntity);
  }

  @Override
  public Optional<TaskList> findById(Long taskListId){
    return taskListJpaRepository.findById(taskListId)
      .map(taskListMapper::toDomain);
  }

  @Override
  public void deleteById(Long id) {
    taskListJpaRepository.deleteById(id);
  }
}
