package com.chaostamer.infrastructure.adapter.out.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chaostamer.infrastructure.adapter.out.persistence.entity.CardEntity;

import java.util.List;

public interface CardJpaRepository extends JpaRepository<CardEntity, Long> {
  
  // Spring Data build the query: "findAll" with the field "taskList" and "id"
  List<CardEntity> findAllByTaskListId(Long taskListId);
}
