package com.chaostamer.infrastructure.adapter.out.persistence;

import lombok.RequiredArgsConstructor; // Constructor injection
import org.springframework.stereotype.Component;

import com.chaostamer.domain.model.User;
import com.chaostamer.domain.port.out.UserRepositoryPort;
import com.chaostamer.infrastructure.adapter.out.persistence.entity.UserEntity;
import com.chaostamer.infrastructure.adapter.out.persistence.mapper.UserPersistenceMapper;
import com.chaostamer.infrastructure.adapter.out.persistence.repository.UserJpaRepository;

import java.util.Optional;

@Component // Spring Bean
@RequiredArgsConstructor // Constructor injection
public class UserPersistenceAdapter implements UserRepositoryPort {

  // Depends on JPA repository and Mapper
  private final UserJpaRepository userJpaRepository;
  private final UserPersistenceMapper userMapper;

  // Implements methods from UserRepositoryPort
  @Override
  public User save(User user) {
    // Translate Domain Model to Entity
    UserEntity userEntity = userMapper.toEntity(user);
    // Save Entity using JPA repository
    UserEntity savedEntity = userJpaRepository.save(userEntity);
    // Translate back to Domain Model and return
    return userMapper.toDomain(savedEntity);
  }

  @Override
  public Optional<User> findByEmail(String email) {
    // Find with JPA (returns Optional<UserEntity>)
    // Map the result to Optional<User>
    return userJpaRepository.findByEmail(email)
          .map(userMapper::toDomain);
  }

  @Override
  public Optional<User> findById(Long id) {
    return userJpaRepository.findById(id)
            .map(userMapper::toDomain);
  }

  @Override
  public boolean existsByEmail(String email) {
    return userJpaRepository.existsByEmail(email);
  }
  
}
