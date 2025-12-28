package com.chaostamer.infrastructure.adapter.out.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chaostamer.infrastructure.adapter.out.persistence.entity.TaskListEntity;

import java.util.List;

public interface TaskListJpaRepository extends JpaRepository<TaskListEntity, Long> {

  List<TaskListEntity> findAllByBoardId(Long boardId);
  
}
