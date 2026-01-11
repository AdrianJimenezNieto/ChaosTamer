import { useEffect, useRef } from "react";
import { Client } from "@stomp/stompjs";
import { useAuthStore } from "../store/authStore";
import type { WsEvent, CardMovePayload } from "../types/api.types";

export const useBoardWebSocket = (
  boardId: string | undefined,
  onEventReceived: (event: WsEvent) => void
) => {
  const clientRef = useRef<Client | null>(null);

  useEffect(() => {
    if (!boardId) return;

    // Get the token
    const token = useAuthStore.getState().token;

    // Client config
    const client = new Client({
      brokerURL: 'ws://localhost:8080/ws',
      reconnectDelay: 5000,
      connectHeaders: {
        Authorization: `Bearer ${token}`
      },
      onConnect: () => {
        console.log('🪢 Conectado al WebSocket del Tablero' + boardId);

        client.subscribe(`/topic/board/${boardId}`, (message) => {
          try {
            const event: WsEvent = JSON.parse(message.body);
            onEventReceived(event);
          } catch (error) {
            console.error("Error parseando mensaje WS", error);
          }
        });
      },
      onStompError: (frame) => {
        console.error("Broker error: '" + frame.headers['message']);
      }
    });

    client.activate();
    clientRef.current = client;

    // Clean when component dismount
    return () => {
      client.deactivate();
    };
  }, [boardId]); // Reconnects when the board changes

  // Function to send the movement
  const sendCardMove = (payload: CardMovePayload) => {
    if (clientRef.current?.connected && boardId) {
      clientRef.current.publish({
        destination: '/app/card.move',
        body: JSON.stringify({
          type: 'CARD_MOVE',
          boardId: Number(boardId),
          payload: payload
        })
      });
    } else {
      console.warn("⚠️ No se pudo enviar el mensaje WS: Desconectado");
    }
  }

  return { sendCardMove };
}