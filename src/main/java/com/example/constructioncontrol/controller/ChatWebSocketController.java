package com.example.constructioncontrol.controller;

import com.example.constructioncontrol.dto.SendMessageRequest;
import com.example.constructioncontrol.dto.WebSocketMessageDTO;
import com.example.constructioncontrol.service.ChatService;
import jakarta.validation.Valid;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

import java.security.Principal;
import java.util.Collections;

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
     * @param principal Principal пользователя из WebSocket сессии (устанавливается в WebSocketAuthInterceptor)
     */
    @MessageMapping("/chat/{orderId}/send")
    public void handleWebSocketMessage(
            @Valid @Payload SendMessageRequest request,
            @DestinationVariable Long orderId,
            Principal principal) {
        // Получаем логин из Principal
        if (principal == null) {
            throw new RuntimeException("User is not authenticated - Principal is null");
        }
        
        String login = principal.getName();
        
        // Устанавливаем SecurityContext для текущего потока на основе Principal
        // Это необходимо, так как SecurityContext может быть потерян между потоками WebSocket
        Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
        if (currentAuth == null || !login.equals(currentAuth.getName())) {
            // Если Principal - это UsernamePasswordAuthenticationToken, используем его напрямую
            if (principal instanceof UsernamePasswordAuthenticationToken) {
                UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) principal;
                SecurityContext context = new SecurityContextImpl();
                context.setAuthentication(auth);
                SecurityContextHolder.setContext(context);
            } else {
                // Иначе создаем базовую аутентификацию (роль будет получена из БД в UserService)
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(
                        login,
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                    );
                SecurityContext context = new SecurityContextImpl();
                context.setAuthentication(authentication);
                SecurityContextHolder.setContext(context);
            }
        }
        
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

