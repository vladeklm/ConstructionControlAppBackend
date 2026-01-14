package com.example.constructioncontrol.repository;

import com.example.constructioncontrol.model.StagePhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StagePhotoRepository extends JpaRepository<StagePhoto, Long> {
    Optional<StagePhoto> findByIdAndStageReportId(Long id, Long stageReportId);
}