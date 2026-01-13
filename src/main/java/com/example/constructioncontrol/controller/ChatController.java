package com.example.constructioncontrol.controller;

import com.example.constructioncontrol.dto.ChatMessageDTO;
import com.example.constructioncontrol.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST контроллер для получения истории сообщений чата.
 * Отправка сообщений осуществляется только через WebSocket.
 */
@RestController
@RequestMapping("/api/orders/{orderId}/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    /**
     * Получение истории сообщений чата по заказу.
     * Сообщения отсортированы по дате создания (от старых к новым).
     * 
     * @param orderId ID заказа
     * @return список сообщений чата
     */
    @GetMapping
    public ResponseEntity<List<ChatMessageDTO>> getChatMessages(@PathVariable Long orderId) {
        List<ChatMessageDTO> messages = chatService.getChatMessages(orderId);
        return ResponseEntity.ok(messages);
    }
}

