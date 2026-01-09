package com.chaostamer.infrastructure.adapter.in.web.controller;

import org.springframework.stereotype.Controller;

import com.chaostamer.domain.port.in.ReorderCardUseCase;
import com.chaostamer.infrastructure.adapter.in.web.dto.ReorderCardRequest;
import com.chaostamer.infrastructure.adapter.in.web.dto.ws.CardMovePayload;
import com.chaostamer.infrastructure.adapter.in.web.dto.ws.WsEvent;

import java.security.Principal;
import java.util.Collections;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class WebSocketController {
  
  private final SimpMessagingTemplate messagingTemplate;
  private final ReorderCardUseCase reorderCardUseCase;

  @MessageMapping("/card.move")
  public void moveCard(@Payload WsEvent<CardMovePayload> event, Principal principal) {
    log.info("Recibiendo evento de movimiento: {}", event);

    // Map the Payload of the WS into UseCase request
    CardMovePayload payload = event.getPayload();

    ReorderCardRequest request = new ReorderCardRequest();
    request.setCardId(payload.getCardId());
    request.setNewTaskListId(payload.getTargetListId());
    request.setNewCardOrder(payload.getNewPosition());

    // Invoke the business logic
    try {
      reorderCardUseCase.reorderCards(Collections.singletonList(request), principal.getName());

      // If success send the event to all the listeners
      String destination = "/topic/board/" + event.getBoardId();

      // Resend the same event to all the subscribers
      messagingTemplate.convertAndSend(destination, event);
      log.info("Evento difundido a: {}", destination);
    } catch (Exception e) {
      log.error("Error moviendo tarjeta: ", e);
    }
  }
}