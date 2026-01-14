package com.example.constructioncontrol.service;

import com.example.constructioncontrol.dto.CreateStageReportRequest;
import com.example.constructioncontrol.dto.StageCompleteResponse;
import com.example.constructioncontrol.dto.StageReportResponse;
import com.example.constructioncontrol.dto.StageResponse;

import java.util.List;

public interface StageService {

    List<StageResponse> getStagesForObject(Long objectId, String sort);

    StageResponse getStageDetails(Long stageId);

    StageResponse approveByCustomer(Long stageId, String comment);

    StageResponse rejectByCustomer(Long stageId, String comment);   // НОВОЕ

    StageCompleteResponse completeStage(Long stageId, String comment);

    List<StageReportResponse> getStageReports(Long stageId);
    StageReportResponse getStageReport(Long stageId, Long reportId);
    StageReportResponse createStageReport(Long stageId, CreateStageReportRequest request);

    StageReportResponse addPhotoToReport(Long stageId, Long reportId, String photoUrl, String caption);
    StageReportResponse removePhotoFromReport(Long stageId, Long reportId, Long photoId);
}
