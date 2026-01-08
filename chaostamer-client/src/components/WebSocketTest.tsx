import React, { useState, useEffect } from 'react'
import SockJS from 'sockjs-client'
import Stomp from 'stompjs';

export const WebSocketTest = () => {

  const [stompClient, setStompClient] = useState<Stomp.Client | null>(null);
  const [messages, setMessages] = useState<string[]>([]);
  const [inputValue, setInputValue] = useState('');

  useEffect(() => {

    // 1. Connect the endpoint defined in Nginx y SpringBoot
    const socket = new SockJS('http://chaostamer.duckdns.org/ws-chaostamer');
    const client = Stomp.over(socket);

    client.connect({}, () => {
      console.log("✅ Conectado al WebSocket")

      // 2. Subscribe to the test channel
      client.subscribe('/topic/greetings', (msg) => {
        setMessages((prev) => [...prev, msg.body]);
      });
    }, (error) => {
      console.error("❌ Error de conexión:", error);
    });

    setStompClient(client);

    // Clean the connexion when the component dismount
    return () => {
      if (client) client.disconnect(() => console.log("Desconectado"));
    }

  }, []);

  const sendMessage = () => {
    if (stompClient && inputValue) {
      // 3. Send message to the prefix /app/hello defined on the backend
      stompClient.send("/app/hello", {}, inputValue);
      setInputValue('');
    }
  };

  return (
    <div style={{ padding: '20px', border: '1px solid #ccc', margin: '20px' }}>
      <h3>Prueba de WebSockets (Semana 2)</h3>
      <input
        value={inputValue}
        onChange={(e) => setInputValue(e.target.value)}
        placeholder="Escribe algo..."
      />
      <button onClick={sendMessage}>Enviar a todos</button>

      <h4>Mensajes recibidos:</h4>
      <ul>
        {messages.map((m, i) => <li key={i}>{m}</li>)}
      </ul>
    </div>
  );
}
