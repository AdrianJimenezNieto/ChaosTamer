package com.chaostamer.domain.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager; // Manager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.chaostamer.domain.exception.EmailAlreadyExistsException;
import com.chaostamer.domain.model.User;
import com.chaostamer.domain.port.in.GetUserUseCase;
import com.chaostamer.domain.port.in.LoginUserUseCase;
import com.chaostamer.domain.port.in.RegisterUserUseCase;
import com.chaostamer.domain.port.out.UserRepositoryPort;
import com.chaostamer.infrastructure.adapter.out.security.jwt.JwtTokenProvider;

// User Service implements the in port
// Here stands the buisness logic for user-related operations
@Service
@RequiredArgsConstructor
public class UserService implements RegisterUserUseCase, LoginUserUseCase, GetUserUseCase {
  
  // We depend on the out port (UserRepositoryPort), not the interface
  private final UserRepositoryPort userRepositoryPort;
  private final PasswordEncoder passwordEncoder;

  // Beans for JWT
  private final AuthenticationManager authenticationManager;
  private final JwtTokenProvider tokenProvider;

  @Override
  public User registerUser(User user) {

    // Verify if the email is already taken
    if (userRepositoryPort.existsByEmail(user.getEmail())) {
      throw new EmailAlreadyExistsException("El email " + user.getEmail() + " ya está registrado.");
    }

    // Hash the password before saving
    String hashedPassword = passwordEncoder.encode(user.getPassword());

    // Set the hashed password to the user object
    user.setPassword(hashedPassword);

    // Persist the user using the out port
    return userRepositoryPort.save(user);
  }

  // Method for the jwt authentication
  @Override
  public String login(LoginCommand command) {
    // Create an authentication try
    Authentication authentication = authenticationManager.authenticate(
      new UsernamePasswordAuthenticationToken(
        command.getEmail(),
        command.getPassword()
      )
    );

    // If the manager doesnt throw exception the authentication is succesful
    SecurityContextHolder.getContext().setAuthentication(authentication);

    // Generate and return the token
    return tokenProvider.generateToken(authentication);
  }

  @Override
  public User getUser(String username) {
    // Get the user
    User user = userRepositoryPort.findByEmail(username)
      .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

    return user;
  }
}
