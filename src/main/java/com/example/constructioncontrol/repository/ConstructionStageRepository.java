package com.example.constructioncontrol.repository;

import com.example.constructioncontrol.model.ConstructionStage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface ConstructionStageRepository extends JpaRepository<ConstructionStage, Long> {

    Optional<ConstructionStage> findFirstByConstructionObjectIdOrderByOrderIndexAsc(Long constructionObjectId);

    List<ConstructionStage> findByConstructionObjectId(Long constructionObjectId);

    Optional<ConstructionStage>
    findFirstByConstructionObjectIdAndOrderIndexGreaterThanOrderByOrderIndexAsc(
            Long constructionObjectId,
            Integer orderIndex
    );
}