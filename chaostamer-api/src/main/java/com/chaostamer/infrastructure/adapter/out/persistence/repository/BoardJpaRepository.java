package com.chaostamer.infrastructure.adapter.out.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chaostamer.infrastructure.adapter.out.persistence.entity.BoardEntity;

import java.util.List;

public interface BoardJpaRepository extends JpaRepository<BoardEntity, Long> {
  
  // Spring JPA creates the SQL query
  List<BoardEntity> findAllByOwnerId(Long userId);
}
