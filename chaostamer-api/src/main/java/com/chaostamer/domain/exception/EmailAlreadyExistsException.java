package com.chaostamer.domain.exception;

// Custom Exception
public class EmailAlreadyExistsException extends RuntimeException {
  public EmailAlreadyExistsException(String message) {
    super(message);
  }
}
