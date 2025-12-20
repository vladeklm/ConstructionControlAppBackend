package com.example.constructioncontrol.controller;

import com.example.constructioncontrol.dto.SendMessageRequest;
import com.example.constructioncontrol.dto.WebSocketMessageDTO;
import com.example.constructioncontrol.service.ChatService;
import jakarta.validation.Valid;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

/**
 * WebSocket контроллер для чата.
 * Единственный способ отправки сообщений в чат - через WebSocket.
 * 
 * Подключение: ws://localhost:8080/ws
 * Отправка сообщения: /app/chat/{orderId}/send
 * Подписка на сообщения: /topic/orders/{orderId}/chat
 */
@Controller
@Validated
public class ChatWebSocketController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatWebSocketController(ChatService chatService, SimpMessagingTemplate messagingTemplate) {
        this.chatService = chatService;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Обработчик входящих сообщений через WebSocket.
     * 
     * Формат отправки: /app/chat/{orderId}/send
     * Тело запроса: { "content": "текст сообщения" }
     * 
     * Сообщение сохраняется в БД и автоматически отправляется всем подписчикам
     * через топик /topic/orders/{orderId}/chat
     * 
     * @param request тело сообщения с содержимым
     * @param orderId ID заказа из пути сообщения
     */
    @MessageMapping("/chat/{orderId}/send")
    public void handleWebSocketMessage(
            @Valid @Payload SendMessageRequest request,
            @DestinationVariable Long orderId) {
        // Сохраняем сообщение через ChatService, который сам отправит его через WebSocket
        chatService.sendMessage(orderId, request);
    }

    /**
     * Отправляет сообщение всем подписанным на чат заказа.
     * Используется внутри ChatService после сохранения сообщения в БД.
     * 
     * @param orderId ID заказа
     * @param message сообщение для отправки
     */
    public void sendMessageToOrderChat(Long orderId, WebSocketMessageDTO message) {
        messagingTemplate.convertAndSend("/topic/orders/" + orderId + "/chat", message);
    }
}

