package com.chaostamer.domain.port.out;

import java.util.Optional;

import com.chaostamer.domain.model.User;

// Port interface for User repository operations
public interface UserRepositoryPort {
  
  User save(User user);

  Optional<User> findByEmail(String email);

  Optional<User> findById(Long id);

  boolean existsByEmail(String email);

  // TODO: Add 'delete', 'update',...
}
