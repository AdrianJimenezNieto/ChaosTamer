package com.chaostamer.infrastructure.config;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import com.chaostamer.infrastructure.adapter.out.security.jwt.JwtTokenProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements ChannelInterceptor {
  
  private final JwtTokenProvider jwtTokenProvider;
  private final UserDetailsService userDetailsService;

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

    if(accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {

      // Extract the token form the header 'Authorization'
      String authHeader = accessor.getFirstNativeHeader("Authorization");

      if (authHeader != null && authHeader.startsWith("Bearer ")) {
        String token = authHeader.substring(7);

        // Validate the token
        if (jwtTokenProvider.validateToken(token)) {
          String username = jwtTokenProvider.getEmailFromToken(token);

          // Load the userDetails
          UserDetails userDetails = userDetailsService.loadUserByUsername(username);

          // Create the authentication
          UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

          // Inyect the identity in the WebSocket Context
          accessor.setUser(authentication);
          log.info("Usuario autenticado en WebSocket: {}", username);
        } else {
          log.warn("Token WebSocket inválido");
        }
      } else {
        log.warn("Intento de conexión WebSocket sin token JWT");
      }
    }

    return message;
  }
}
