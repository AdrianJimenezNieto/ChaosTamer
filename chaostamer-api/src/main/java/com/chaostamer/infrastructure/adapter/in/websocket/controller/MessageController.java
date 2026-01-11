package com.chaostamer.infrastructure.adapter.in.websocket.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class MessageController {
  
  // The client send messages to "/app/hello"
  @MessageMapping("/hello")
  // The server responds to all subscribers of "/topic/greetings"
  @SendTo("/topic/greetings")
  public String greeting(String message) {
    return "Servidor dice: Hola! Recibi tu mensaje: " + message;
  }
}
