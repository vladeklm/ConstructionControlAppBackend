package com.example.constructioncontrol.service.impl;

import com.example.constructioncontrol.dto.StageCompleteResponse;
import com.example.constructioncontrol.dto.StageResponse;
import com.example.constructioncontrol.model.ConstructionObject;
import com.example.constructioncontrol.model.ConstructionStage;
import com.example.constructioncontrol.model.ConstructionStageStatus;
import com.example.constructioncontrol.model.DocumentStatus;
import com.example.constructioncontrol.model.HighLevelStage;
import com.example.constructioncontrol.model.OrderStatus;
import com.example.constructioncontrol.model.ProjectOrder;
import com.example.constructioncontrol.model.StageType;
import com.example.constructioncontrol.model.UserAccount;
import com.example.constructioncontrol.model.UserRole;
import com.example.constructioncontrol.repository.ConstructionObjectRepository;
import com.example.constructioncontrol.repository.ConstructionStageRepository;
import com.example.constructioncontrol.repository.DocumentRepository;
import com.example.constructioncontrol.repository.ProjectOrderRepository;
import com.example.constructioncontrol.service.StageService;
import com.example.constructioncontrol.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class StageServiceImpl implements StageService {

    private final ConstructionStageRepository constructionStageRepository;
    private final ConstructionObjectRepository constructionObjectRepository;
    private final DocumentRepository documentRepository;
    private final ProjectOrderRepository projectOrderRepository;
    private final UserService userService;

    public StageServiceImpl(ConstructionStageRepository constructionStageRepository,
                            ConstructionObjectRepository constructionObjectRepository,
                            DocumentRepository documentRepository,
                            ProjectOrderRepository projectOrderRepository,
                            UserService userService) {
        this.constructionStageRepository = constructionStageRepository;
        this.constructionObjectRepository = constructionObjectRepository;
        this.documentRepository = documentRepository;
        this.projectOrderRepository = projectOrderRepository;
        this.userService = userService;
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
}
