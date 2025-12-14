package com.example.constructioncontrol.repository;

import com.example.constructioncontrol.model.Document;
import com.example.constructioncontrol.model.DocumentStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.constructioncontrol.model.DocumentType;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findByStageId(Long stageId);

    List<Document> findByConstructionObjectId(Long constructionObjectId);

    List<Document> findByConstructionObjectIdAndStatus(
            Long constructionObjectId,
            DocumentStatus status
    );

    boolean existsByStageIdAndType(Long stageId, DocumentType type);
}
