package com.chaostamer.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer{

  // Interceptor inyection
  private final WebSocketAuthInterceptor webSocketAuthInterceptor;
  
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
    registry.addEndpoint("/ws")
      .setAllowedOriginPatterns("*"); // Cambia esto para desarrollo local
      // .setAllowedOrigins("https://chaostamer.duckdns.org") // Security: only allow this origin
  }

  @Override
  public void configureClientInboundChannel(ChannelRegistration registration) {
    // Interceptor to the in channel
    registration.interceptors(webSocketAuthInterceptor);
  }
}
