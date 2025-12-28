package com.chaostamer.infrastructure.adapter.out.persistence.mapper;

import org.springframework.stereotype.Component;

import com.chaostamer.domain.model.User;
import com.chaostamer.infrastructure.adapter.out.persistence.entity.UserEntity;

@Component // We mark it as a Bean for Spring to manage
public class UserPersistenceMapper {
  
  // Convert Entity to Domain Model
  public User toDomain(UserEntity entity) {
    if (entity == null) {
      return null;
    }
    return User.builder()
          .id(entity.getId())
          .name(entity.getName())
          .lastName(entity.getLastName())
          .email(entity.getEmail())
          .password(entity.getPassword())
          .build();
  }

  // Convert Domain Model (POJO) to Entity (JPA)
  public UserEntity toEntity(User domain) {
    if(domain == null) {
      return null;
    }

    UserEntity entity = new UserEntity();
    entity.setId(domain.getId());
    entity.setName(domain.getName());
    entity.setLastName(domain.getLastName());
    entity.setEmail(domain.getEmail());
    entity.setPassword(domain.getPassword());
    return entity;
  }
}
