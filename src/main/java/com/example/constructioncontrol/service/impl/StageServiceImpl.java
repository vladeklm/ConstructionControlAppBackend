package com.example.constructioncontrol.service.impl;

import com.example.constructioncontrol.dto.CreateStageReportRequest;
import com.example.constructioncontrol.dto.StageCompleteResponse;
import com.example.constructioncontrol.dto.StageReportResponse;
import com.example.constructioncontrol.dto.StageResponse;
import com.example.constructioncontrol.model.*;
import com.example.constructioncontrol.repository.*;
import com.example.constructioncontrol.service.StageService;
import com.example.constructioncontrol.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class StageServiceImpl implements StageService {

    private final ConstructionStageRepository constructionStageRepository;
    private final ConstructionObjectRepository constructionObjectRepository;
    private final DocumentRepository documentRepository;
    private final ProjectOrderRepository projectOrderRepository;
    private final UserService userService;
    private final StageReportRepository stageReportRepository;
    private final StagePhotoRepository stagePhotoRepository;

    public StageServiceImpl(ConstructionStageRepository constructionStageRepository,
                            ConstructionObjectRepository constructionObjectRepository,
                            DocumentRepository documentRepository,
                            ProjectOrderRepository projectOrderRepository,
                            UserService userService, StageReportRepository stageReportRepository, StagePhotoRepository stagePhotoRepository) {
        this.constructionStageRepository = constructionStageRepository;
        this.constructionObjectRepository = constructionObjectRepository;
        this.documentRepository = documentRepository;
        this.projectOrderRepository = projectOrderRepository;
        this.userService = userService;
        this.stageReportRepository = stageReportRepository;
        this.stagePhotoRepository = stagePhotoRepository;
    }

    @Override
    public List<StageResponse> getStagesForObject(Long objectId, String sort) {
        ConstructionObject object = constructionObjectRepository.findById(objectId)
                .orElseThrow(() -> new EntityNotFoundException("Construction object not found"));

        List<ConstructionStage> stages =
                constructionStageRepository.findByConstructionObjectId(object.getId());

        Comparator<ConstructionStage> cmp =
                Comparator.comparing(ConstructionStage::getOrderIndex);
        if ("DESC".equalsIgnoreCase(sort)) {
            cmp = cmp.reversed();
        }

        return stages.stream()
                .sorted(cmp)
                .map(this::toStageResponse)
                .toList();
    }

    @Override
    public StageResponse getStageDetails(Long stageId) {
        ConstructionStage stage = constructionStageRepository.findById(stageId)
                .orElseThrow(() -> new EntityNotFoundException("Stage not found"));
        return toStageResponse(stage);
    }

    @Transactional
    @Override
    public StageResponse approveByCustomer(Long stageId, String comment) {
        ConstructionStage stage = constructionStageRepository.findById(stageId)
                .orElseThrow(() -> new EntityNotFoundException("Stage not found"));

        UserAccount current = userService.getCurrentUserAccount();
        ConstructionObject object = stage.getConstructionObject();
        if (object == null || object.getCustomer() == null
                || !object.getCustomer().getId().equals(current.getId())) {
            throw new AccessDeniedException("Only object owner can approve stage");
        }

        if (stage.getStatus() == ConstructionStageStatus.NOT_STARTED) {
            stage.setStatus(ConstructionStageStatus.IN_PROGRESS);
            stage.setActualStartDate(LocalDate.now());
            stage.setProgressPercentage(0);
        } else if (stage.getStatus() == ConstructionStageStatus.IN_PROGRESS) {
            stage.setStatus(ConstructionStageStatus.COMPLETED);
            stage.setProgressPercentage(100);
            stage.setActualEndDate(LocalDate.now());
        } else {
            throw new IllegalStateException("Stage cannot be approved in current status");
        }

        stage = constructionStageRepository.save(stage);
        return toStageResponse(stage);
    }

    @Transactional
    @Override
    public StageCompleteResponse completeStage(Long stageId, String comment) {
        ConstructionStage stage = constructionStageRepository.findById(stageId)
                .orElseThrow(() -> new EntityNotFoundException("Stage not found"));

        UserAccount current = userService.getCurrentUserAccount();
        if (!current.getRole().equals(UserRole.ENGINEER)) {
            throw new AccessDeniedException("Only engineer can complete stage");
        }

        if (stage.getStatus() == ConstructionStageStatus.NEEDS_ATTENTION) {
            throw new IllegalStateException("Stage has unresolved issues (NEEDS_ATTENTION)");
        }

        if (stage.getStatus() != ConstructionStageStatus.IN_PROGRESS) {
            throw new IllegalStateException("Stage must be IN_PROGRESS to complete");
        }

        stage.setStatus(ConstructionStageStatus.COMPLETED);
        stage.setProgressPercentage(100);
        stage.setActualEndDate(LocalDate.now());
        stage = constructionStageRepository.save(stage);

        updateOrderStatusIfCompleted(stage.getConstructionObject());

        ConstructionStage next = constructionStageRepository
                .findFirstByConstructionObjectIdAndOrderIndexGreaterThanOrderByOrderIndexAsc(
                        stage.getConstructionObject().getId(),
                        stage.getOrderIndex()
                ).orElse(null);

        StageCompleteResponse resp = new StageCompleteResponse();
        resp.setCompletedStage(toStageResponse(stage));
        resp.setNextStage(next != null ? toStageResponse(next) : null);
        return resp;
    }

    private StageResponse toStageResponse(ConstructionStage stage) {
        StageResponse dto = new StageResponse();
        dto.setId(stage.getId());
        dto.setType(stage.getType().name());
        dto.setStatus(stage.getStatus().name());
        dto.setHighLevelStage(mapStageTypeToHighLevel(stage.getType()).name());
        dto.setProgressPercentage(stage.getProgressPercentage());
        dto.setPlannedStartDate(stage.getPlannedStartDate());
        dto.setPlannedEndDate(stage.getPlannedEndDate());
        dto.setActualStartDate(stage.getActualStartDate());
        dto.setActualEndDate(stage.getActualEndDate());
        dto.setOrder(stage.getOrderIndex());
        return dto;
    }

    private HighLevelStage mapStageTypeToHighLevel(StageType type) {
        return switch (type) {
            case PREPARATION -> HighLevelStage.DOCS_APPROVAL;
            case HANDOVER    -> HighLevelStage.COMPLETION;
            default          -> HighLevelStage.BUILDING;
        };
    }

    // --- автообновление статуса заказа ---

    private void updateOrderStatusIfCompleted(ConstructionObject object) {
        if (object == null) {
            return;
        }

        boolean allStagesCompleted = constructionStageRepository
                .findByConstructionObjectId(object.getId())
                .stream()
                .allMatch(s -> s.getStatus() == ConstructionStageStatus.COMPLETED);

        if (!allStagesCompleted) {
            return;
        }

        boolean allDocsSigned = documentRepository
                .findByConstructionObjectId(object.getId())
                .stream()
                .allMatch(d -> d.getStatus() == DocumentStatus.SIGNED);

        if (!allDocsSigned) {
            return;
        }

        ProjectOrder order = projectOrderRepository
                .findByConstructionObjectId(object.getId())
                .orElse(null);

        if (order != null && order.getStatus() != OrderStatus.COMPLETED) {
            order.setStatus(OrderStatus.COMPLETED);
            projectOrderRepository.save(order);
        }
    }

    @Transactional
    @Override
    public StageResponse rejectByCustomer(Long stageId, String comment) {
        ConstructionStage stage = constructionStageRepository.findById(stageId)
                .orElseThrow(() -> new EntityNotFoundException("Stage not found"));

        UserAccount current = userService.getCurrentUserAccount();
        ConstructionObject object = stage.getConstructionObject();
        if (object == null || object.getCustomer() == null
                || !object.getCustomer().getId().equals(current.getId())) {
            throw new AccessDeniedException("Only object owner can reject stage");
        }

        if (stage.getStatus() != ConstructionStageStatus.IN_PROGRESS
                && stage.getStatus() != ConstructionStageStatus.COMPLETED) {
            throw new IllegalStateException("Stage cannot be rejected in current status");
        }

        stage.setStatus(ConstructionStageStatus.NEEDS_ATTENTION);
        // здесь можно сохранить comment в отдельную таблицу замечаний/истории

        stage = constructionStageRepository.save(stage);
        return toStageResponse(stage);
    }

    @Override
    public List<StageReportResponse>  getStageReports(Long stageId) {
        ConstructionStage stage = constructionStageRepository.findById(stageId)
                .orElseThrow(() -> new EntityNotFoundException("Stage not found"));
        List<StageReport> reports = stageReportRepository.findByStageIdOrderByReportDateDesc(stageId);
        return reports.stream()
                .map(this::toStageReportResponse)
                .collect(Collectors.toList());
    }

    @Override
    public StageReportResponse getStageReport(Long stageId, Long reportId) {
        StageReport report = stageReportRepository.findByIdAndStageId(reportId, stageId)
                .orElseThrow(() -> new EntityNotFoundException("Stage report not found"));

        return toStageReportResponse(report);
    }

    @Transactional
    @Override
    public StageReportResponse createStageReport(Long stageId, CreateStageReportRequest request) {
        ConstructionStage stage = constructionStageRepository.findById(stageId)
                .orElseThrow(() -> new EntityNotFoundException("Stage not found"));

        UserAccount current = userService.getCurrentUserAccount();
        if (!current.getRole().equals(UserRole.ENGINEER)) {
            throw new AccessDeniedException("Only engineer can create stage report");
        }

        StageReport report = new StageReport();
        report.setTitle(request.getTitle());
        report.setReportDate(request.getReportDate() != null ? request.getReportDate() : LocalDate.now());
        report.setStatus(request.getStatus() != null ? request.getStatus() : StageReportStatus.OK);
        report.setComment(request.getComment());
        report.setPdfUrl(request.getPdfUrl());
        report.setAuthor(current);
        report.setStage(stage);

        report = stageReportRepository.save(report);
        return toStageReportResponse(report);
    }

    @Transactional
    @Override
    public StageReportResponse addPhotoToReport(Long stageId, Long reportId, String photoUrl, String caption) {
        StageReport report = stageReportRepository.findByIdAndStageId(reportId, stageId)
                .orElseThrow(() -> new EntityNotFoundException("Stage report not found"));

        UserAccount current = userService.getCurrentUserAccount();
        if (!report.getAuthor().getId().equals(current.getId())) {
            throw new AccessDeniedException("Only report author can add photos");
        }

        StagePhoto photo = new StagePhoto();
        photo.setUrl(photoUrl);
        photo.setCaption(caption);
        photo.setStageReport(report);

        stagePhotoRepository.save(photo);

        // Refresh report with photos
        report = stageReportRepository.findById(reportId)
                .orElseThrow(() -> new EntityNotFoundException("Stage report not found"));

        return toStageReportResponse(report);
    }

    @Transactional
    @Override
    public StageReportResponse removePhotoFromReport(Long stageId, Long reportId, Long photoId) {
        StagePhoto photo = stagePhotoRepository.findByIdAndStageReportId(photoId, reportId)
                .orElseThrow(() -> new EntityNotFoundException("Photo not found"));
        stagePhotoRepository.delete(photo);

        // Refresh report with photos
        StageReport report = stageReportRepository.findByIdAndStageId(reportId, stageId)
                .orElseThrow(() -> new EntityNotFoundException("Stage report not found"));

        return toStageReportResponse(report);
    }

    private StageReportResponse toStageReportResponse(StageReport report) {
        StageReportResponse dto = new StageReportResponse();
        dto.setId(report.getId());
        dto.setTitle(report.getTitle());
        dto.setReportDate(OffsetDateTime.from(report.getReportDate()));
        dto.setStatus(report.getStatus().name());
        dto.setComment(report.getComment());
        dto.setPdfUrl(report.getPdfUrl());

        if (report.getAuthor() != null) {
            StageReportResponse.AuthorInfo author = new StageReportResponse.AuthorInfo();
            author.setId(report.getAuthor().getId());
            author.setFullName(report.getAuthor().getFullName());
            author.setEmail(report.getAuthor().getEmail());
            dto.setAuthor(author);
        }

        dto.setPhotos(report.getPhotos().stream()
                .map(photo -> {
                    StageReportResponse.PhotoInfo photoInfo = new StageReportResponse.PhotoInfo();
                    photoInfo.setId(photo.getId());
                    photoInfo.setPhotoUrl(photo.getUrl());
                    photoInfo.setCaption(photo.getCaption());
                    photoInfo.setUploadedAt(photo.getUpdatedAt());
                    return photoInfo;
                })
                .collect(Collectors.toList()));

        return dto;
    }
}
