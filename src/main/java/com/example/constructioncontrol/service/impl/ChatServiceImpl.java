package com.example.constructioncontrol.service.impl;

import com.example.constructioncontrol.dto.ChatMessageDTO;
import com.example.constructioncontrol.dto.SendMessageRequest;
import com.example.constructioncontrol.dto.WebSocketMessageDTO;
import com.example.constructioncontrol.model.ChatMessage;
import com.example.constructioncontrol.model.ChatThread;
import com.example.constructioncontrol.model.MessageType;
import com.example.constructioncontrol.model.ProjectOrder;
import com.example.constructioncontrol.model.UserAccount;
import com.example.constructioncontrol.model.UserRole;
import com.example.constructioncontrol.repository.ChatMessageRepository;
import com.example.constructioncontrol.repository.ChatThreadRepository;
import com.example.constructioncontrol.repository.ProjectOrderRepository;
import com.example.constructioncontrol.service.ChatService;
import com.example.constructioncontrol.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ChatServiceImpl implements ChatService {

    private final ChatThreadRepository chatThreadRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ProjectOrderRepository projectOrderRepository;
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatServiceImpl(ChatThreadRepository chatThreadRepository,
                          ChatMessageRepository chatMessageRepository,
                          ProjectOrderRepository projectOrderRepository,
                          UserService userService,
                          SimpMessagingTemplate messagingTemplate) {
        this.chatThreadRepository = chatThreadRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.projectOrderRepository = projectOrderRepository;
        this.userService = userService;
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatMessageDTO> getChatMessages(Long orderId) {
        UserAccount currentUser = userService.getCurrentUserAccount();
        
        ProjectOrder order = projectOrderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        // Проверка прав доступа
        checkOrderAccess(order, currentUser);

        // Получаем или создаем чат для заказа
        ChatThread chatThread = chatThreadRepository.findByProjectOrderId(orderId)
                .orElseGet(() -> createChatThread(order, currentUser));

        // Получаем все сообщения чата, отсортированные по дате создания
        List<ChatMessage> messages = chatMessageRepository.findByChatThreadOrderByCreatedAtAsc(chatThread);

        return messages.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ChatMessageDTO sendMessage(Long orderId, SendMessageRequest request) {
        UserAccount currentUser = userService.getCurrentUserAccount();
        
        ProjectOrder order = projectOrderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        // Проверка прав доступа
        checkOrderAccess(order, currentUser);

        // Получаем или создаем чат для заказа
        ChatThread chatThread = chatThreadRepository.findByProjectOrderId(orderId)
                .orElseGet(() -> createChatThread(order, currentUser));

        // Создаем новое сообщение
        ChatMessage message = new ChatMessage();
        message.setChatThread(chatThread);
        message.setSender(currentUser);
        message.setContent(request.getContent());
        message.setType(MessageType.TEXT);

        message = chatMessageRepository.save(message);

        ChatMessageDTO messageDTO = toDTO(message);
        
        // Отправляем сообщение через WebSocket всем подписанным на чат заказа
        WebSocketMessageDTO wsMessage = toWebSocketDTO(message, orderId);
        messagingTemplate.convertAndSend("/topic/orders/" + orderId + "/chat", wsMessage);

        return messageDTO;
    }

    private ChatThread createChatThread(ProjectOrder order, UserAccount currentUser) {
        ChatThread chatThread = new ChatThread();
        chatThread.setProjectOrder(order);
        chatThread.setTopic("Чат по заказу #" + order.getId());
        
        // Добавляем участников: клиента и текущего пользователя (если это не клиент)
        chatThread.getParticipants().add(order.getCustomer());
        if (!order.getCustomer().getId().equals(currentUser.getId())) {
            chatThread.getParticipants().add(currentUser);
        }
        
        return chatThreadRepository.save(chatThread);
    }

    private void checkOrderAccess(ProjectOrder order, UserAccount user) {
        UserRole role = user.getRole();
        
        // ADMIN и MANAGER имеют доступ ко всем заказам
        if (role == UserRole.ADMIN || role == UserRole.MANAGER) {
            return;
        }
        
        // CUSTOMER может видеть только свои заказы
        if (role == UserRole.CUSTOMER) {
            if (order.getCustomer() == null || !order.getCustomer().getId().equals(user.getId())) {
                throw new AccessDeniedException("Access denied to this order");
            }
            return;
        }
        
        // Для других ролей доступ запрещен
        throw new AccessDeniedException("Access denied");
    }

    private ChatMessageDTO toDTO(ChatMessage message) {
        ChatMessageDTO dto = new ChatMessageDTO();
        dto.setId(message.getId());
        dto.setSenderId(message.getSender().getId());
        dto.setSenderName(message.getSender().getFullName());
        dto.setContent(message.getContent());
        dto.setType(message.getType().name());
        // Используем sentAt, если установлен, иначе createdAt из BaseEntity
        OffsetDateTime timestamp = message.getSentAt() != null ? message.getSentAt() : message.getCreatedAt();
        dto.setCreatedAt(timestamp);
        return dto;
    }

    private WebSocketMessageDTO toWebSocketDTO(ChatMessage message, Long orderId) {
        WebSocketMessageDTO dto = new WebSocketMessageDTO();
        dto.setId(message.getId());
        dto.setOrderId(orderId);
        dto.setSenderId(message.getSender().getId());
        dto.setSenderName(message.getSender().getFullName());
        dto.setContent(message.getContent());
        dto.setType(message.getType().name());
        // Используем sentAt, если установлен, иначе createdAt из BaseEntity
        OffsetDateTime timestamp = message.getSentAt() != null ? message.getSentAt() : message.getCreatedAt();
        dto.setCreatedAt(timestamp);
        return dto;
    }
}

