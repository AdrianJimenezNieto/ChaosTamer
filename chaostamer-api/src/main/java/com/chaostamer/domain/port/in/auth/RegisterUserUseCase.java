package com.chaostamer.domain.port.in.auth;

import com.chaostamer.domain.model.User;

// In  Port: defines the use case for registering a user
public interface RegisterUserUseCase {
  
  User registerUser(User user);
}
