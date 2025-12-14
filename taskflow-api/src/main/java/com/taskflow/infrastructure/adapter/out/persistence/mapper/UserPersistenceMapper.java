package com.taskflow.infrastructure.adapter.out.persistence.mapper;

import com.taskflow.domain.model.User;
import com.taskflow.infrastructure.adapter.out.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;

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
