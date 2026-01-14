package com.example.constructioncontrol.controller;

import com.example.constructioncontrol.dto.*;
import com.example.constructioncontrol.model.StageReport;
import com.example.constructioncontrol.service.StageService;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class StageController {

    private final StageService stageService;

    public StageController(StageService stageService) {
        this.stageService = stageService;
    }

    @GetMapping("/objects/{objectId}/stages")
    public StagesResponse getStages(
            @PathVariable Long objectId,
            @RequestParam(defaultValue = "ASC") String sort) {

        List<StageResponse> list = stageService.getStagesForObject(objectId, sort);
        StagesResponse resp = new StagesResponse();
        resp.setStages(list);
        return resp;
    }

    @GetMapping("/stages/{stageId}")
    public StageResponse getStage(@PathVariable Long stageId) {
        return stageService.getStageDetails(stageId);
    }

    @PostMapping("/stages/{stageId}/approve-by-customer")
    public StageResponse approveByCustomer(
            @PathVariable Long stageId,
            @RequestBody(required = false) Map<String, String> body) {
        String comment = body != null ? body.get("comment") : null;
        return stageService.approveByCustomer(stageId, comment);
    }

    @PostMapping("/stages/{stageId}/complete")
    public StageCompleteResponse completeStage(
            @PathVariable Long stageId,
            @RequestBody(required = false) Map<String, String> body) {
        String comment = body != null ? body.get("comment") : null;
        return stageService.completeStage(stageId, comment);
    }

    @PostMapping("/stages/{stageId}/reject-by-customer")
    public StageResponse rejectByCustomer(
            @PathVariable Long stageId,
            @RequestBody Map<String, String> body) {
        String comment = body != null ? body.get("comment") : null;
        return stageService.rejectByCustomer(stageId, comment);
    }

    @GetMapping("/stages/{stageId}/reports")
    public List<StageReportResponse> getStageReports(@PathVariable Long stageId) {
        return stageService.getStageReports(stageId);
    }

    @GetMapping("/stages/{stageId}/reports/{reportId}")
    public StageReportResponse getStageReport(
            @PathVariable Long stageId,
            @PathVariable Long reportId) {
        return stageService.getStageReport(stageId, reportId);
    }

    @PostMapping("/stages/{stageId}/reports")
    public StageReportResponse createStageReport(
            @PathVariable Long stageId,
            @RequestBody CreateStageReportRequest request) {
        return stageService.createStageReport(stageId, request);
    }

    @PostMapping("/stages/{stageId}/reports/{reportId}/photos")
    public StageReportResponse addPhotoToReport(
            @PathVariable Long stageId,
            @PathVariable Long reportId,
            @RequestBody Map<String, String> request) {
        String photoUrl = request.get("photoUrl");
        String caption = request.get("caption");
        return stageService.addPhotoToReport(stageId, reportId, photoUrl, caption);
    }

    @DeleteMapping("/stages/{stageId}/reports/{reportId}/photos/{photoId}")
    public StageReportResponse removePhotoFromReport(
            @PathVariable Long stageId,
            @PathVariable Long reportId,
            @PathVariable Long photoId) {
        return stageService.removePhotoFromReport(stageId, reportId, photoId);
    }

}
