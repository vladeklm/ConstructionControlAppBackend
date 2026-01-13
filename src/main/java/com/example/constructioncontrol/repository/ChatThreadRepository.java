package com.example.constructioncontrol.repository;

import com.example.constructioncontrol.model.ChatThread;
import com.example.constructioncontrol.model.ProjectOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatThreadRepository extends JpaRepository<ChatThread, Long> {

    Optional<ChatThread> findByProjectOrder(ProjectOrder projectOrder);

    Optional<ChatThread> findByProjectOrderId(Long projectOrderId);
}

