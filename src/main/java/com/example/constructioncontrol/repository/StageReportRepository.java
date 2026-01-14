package com.example.constructioncontrol.repository;

import com.example.constructioncontrol.model.StageReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StageReportRepository extends JpaRepository<StageReport, Long> {
    List<StageReport> findByStageIdOrderByReportDateDesc(Long stageId);
    Optional<StageReport> findByIdAndStageId(Long id, Long stageId);
}