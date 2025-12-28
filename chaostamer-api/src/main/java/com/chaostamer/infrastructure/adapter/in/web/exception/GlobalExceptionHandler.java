package com.chaostamer.infrastructure.adapter.in.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.chaostamer.domain.exception.EmailAlreadyExistsException;

import java.util.Map;

@ControllerAdvice // Tells Spring that this class watch all controllers
public class GlobalExceptionHandler {
  
  // Thiis method will activate when an EmailAlreadyExistsException rise
  @ExceptionHandler(EmailAlreadyExistsException.class)
  public ResponseEntity<Object> handleEmailAreadyExists(EmailAlreadyExistsException e) {

    // Create a body for the JSON
    Map<String, String> body = Map.of(
      "error", "Conflict",
      "message", e.getMessage()
    );

    // We return a 409 CONFLICT
    return new ResponseEntity<>(body, HttpStatus.CONFLICT);
  }

}
