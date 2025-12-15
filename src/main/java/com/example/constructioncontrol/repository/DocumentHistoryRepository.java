package com.example.constructioncontrol.repository;

import com.example.constructioncontrol.model.DocumentHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentHistoryRepository extends JpaRepository<DocumentHistory, Long> {

    List<DocumentHistory> findByDocumentIdOrderByTimestampDesc(Long documentId);
}