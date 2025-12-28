package com.chaostamer.infrastructure.adapter.out.security.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.chaostamer.domain.port.out.UserRepositoryPort;

import java.util.Collections; // For the roles

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  // We depend on the domain port, as always
  private final UserRepositoryPort userRepositoryPort;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    // We use the email bc our username can be doubled

    // We search the user on our Database using the port
    com.chaostamer.domain.model.User ourUser = userRepositoryPort.findByEmail(email)
      .orElseThrow(() ->
        new UsernameNotFoundException("Usuario no encontrado con el email: " + email)
      );

    // Conver our User (domain) into User Spring Security
    // Leave Roles empty for now
    return new User(
      ourUser.getEmail(),
      ourUser.getPassword(),
      Collections.emptyList() // Roles here ("ROLE_USER")
    );

  }
  
}
