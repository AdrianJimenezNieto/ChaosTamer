package com.chaostamer.infrastructure.adapter.out.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chaostamer.infrastructure.adapter.out.persistence.entity.UserEntity;

import java.util.Optional;

// Extends JpaRepository
// Talks to UserEntity, not to User domain model
public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {
  
  // Spring JPA builds the query based on method name
  Optional<UserEntity> findByEmail(String email);

  boolean existsByEmail(String email);
}
