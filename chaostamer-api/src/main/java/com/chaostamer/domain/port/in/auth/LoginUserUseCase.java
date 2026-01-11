package com.chaostamer.domain.port.in.auth;

// Simple DTO to pass the data of the login
import lombok.Getter;

public interface LoginUserUseCase {

  String login(LoginCommand command);

  @Getter
  class LoginCommand {
    private final String email;
    private final String password;

    public LoginCommand(String email, String password) {
      this.email = email;
      // TODO: Add validation if want
      this.password = password;
    }
  }
  
}
