package com.chaostamer.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer{
  
  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) {
    // Messages that the server sends to the client
    config.enableSimpleBroker("/topic");
    // Messages that the client sends to the server
    config.setApplicationDestinationPrefixes("/app");
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    // This is the connection endpoint (the "handshake")
    registry.addEndpoint("/ws-chaostamer")
      .setAllowedOrigins("https://chaostamer.duckdns.org") // Security: only allow this origin
      .withSockJS();
  }
}
