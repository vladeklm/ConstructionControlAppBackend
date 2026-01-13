package com.example.constructioncontrol.repository;

import com.example.constructioncontrol.model.HighLevelStage;
import com.example.constructioncontrol.model.StageDocumentRequirement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StageDocumentRequirementRepository extends JpaRepository<StageDocumentRequirement, Long> {

    List<StageDocumentRequirement> findByHighLevelStageOrderByOrderIndex(HighLevelStage highLevelStage);
}