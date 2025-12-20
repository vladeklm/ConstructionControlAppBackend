package com.example.constructioncontrol.service;

import com.example.constructioncontrol.dto.ChatMessageDTO;
import com.example.constructioncontrol.dto.SendMessageRequest;

import java.util.List;

public interface ChatService {

    List<ChatMessageDTO> getChatMessages(Long orderId);

    ChatMessageDTO sendMessage(Long orderId, SendMessageRequest request);
}

