package com.taskflow.infrastructure.adapter.out.persistence;

import com.taskflow.domain.model.TaskList;
import com.taskflow.domain.port.out.TaskListRepositoryPort;
import com.taskflow.infrastructure.adapter.out.persistence.entity.BoardEntity;
import com.taskflow.infrastructure.adapter.out.persistence.entity.TaskListEntity;
import com.taskflow.infrastructure.adapter.out.persistence.mapper.TaskListPersistenceMapper;
import com.taskflow.infrastructure.adapter.out.persistence.repository.TaskListJpaRepository;
import com.taskflow.infrastructure.adapter.out.persistence.repository.BoardJpaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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
  public List<TaskList> findAllByBoard(Long boardId) {
    return taskListJpaRepository.findAllByBoardId(boardId)
      .stream()
      .map(taskListMapper::toDomain)
      .collect(Collectors.toList());
  }

  @Override
  public TaskList save(TaskList taskList) {
    // Map the entity
    TaskListEntity taskListEntity = taskListMapper.toEntity(taskList);
    // Find the owner
    BoardEntity boadEntity = boardJpaRepository.findById(taskList.getBoardId())
      .orElseThrow(() -> new EntityNotFoundException());
    // Assing the board
    taskListEntity.setBoard(boadEntity);
    // Save with the jpa
    TaskListEntity savedEntity = taskListJpaRepository.save(taskListEntity);
    // Remap and return
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
