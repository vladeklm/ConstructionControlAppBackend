package com.example.constructioncontrol.repository;

import com.example.constructioncontrol.model.ChatMessage;
import com.example.constructioncontrol.model.ChatThread;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findByChatThreadOrderByCreatedAtAsc(ChatThread chatThread);
}

