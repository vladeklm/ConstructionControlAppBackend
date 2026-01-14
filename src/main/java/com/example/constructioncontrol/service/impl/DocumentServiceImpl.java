package com.example.constructioncontrol.service.impl;

import com.example.constructioncontrol.dto.document.DocumentChecklistItemResponse;
import com.example.constructioncontrol.dto.document.DocumentChecklistResponse;
import com.example.constructioncontrol.dto.document.DocumentCommentRequest;
import com.example.constructioncontrol.dto.document.DocumentDetailsResponse;
import com.example.constructioncontrol.dto.document.DocumentRejectRequest;
import com.example.constructioncontrol.dto.document.StageDocumentItemResponse;
import com.example.constructioncontrol.dto.document.StageDocumentsResponse;
import com.example.constructioncontrol.model.ConstructionObject;
import com.example.constructioncontrol.model.ConstructionStage;
import com.example.constructioncontrol.model.Document;
import com.example.constructioncontrol.model.DocumentStatus;
import com.example.constructioncontrol.model.HighLevelStage;
import com.example.constructioncontrol.model.StageDocumentRequirement;
import com.example.constructioncontrol.model.StageType;
import com.example.constructioncontrol.model.UserAccount;
import com.example.constructioncontrol.model.UserRole;
import com.example.constructioncontrol.repository.ConstructionObjectRepository;
import com.example.constructioncontrol.repository.ConstructionStageRepository;
import com.example.constructioncontrol.repository.DocumentRepository;
import com.example.constructioncontrol.repository.StageDocumentRequirementRepository;
import com.example.constructioncontrol.service.DocumentService;
import com.example.constructioncontrol.service.UserService;
import com.example.constructioncontrol.repository.DocumentHistoryRepository;
import com.example.constructioncontrol.model.DocumentHistoryAction;
import com.example.constructioncontrol.model.DocumentHistory;
import com.example.constructioncontrol.dto.document.DocumentHistoryResponse;
import com.example.constructioncontrol.dto.document.DocumentHistoryItemResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final ConstructionStageRepository stageRepository;
    private final ConstructionObjectRepository objectRepository;
    private final UserService userService;
    private final StageDocumentRequirementRepository requirementRepository;
    private final DocumentHistoryRepository historyRepository;

    // ---------- Документы по этапу ----------

    @Override
    @Transactional(readOnly = true)
    public StageDocumentsResponse getStageDocuments(Long stageId) {
        ConstructionStage stage = stageRepository.findById(stageId)
                .orElseThrow(() -> new EntityNotFoundException("Stage not found"));

        List<Document> documents = documentRepository.findByStageId(stageId);

        StageDocumentsResponse response = new StageDocumentsResponse();
        response.setStageId(stage.getId());
        response.setStageName(stage.getType().name());
        response.setHighLevelStage(mapStageTypeToHighLevel(stage.getType()).name());

        ConstructionObject object = stage.getConstructionObject();
        if (object != null) {
            response.setConstructionObjectId(object.getId());
            response.setObjectAddress(object.getAddress());
        }

        List<StageDocumentItemResponse> items = documents.stream()
                .map(this::toStageDocumentItem)
                .collect(Collectors.toList());
        response.setDocuments(items);

        return response;
    }

    private StageDocumentItemResponse toStageDocumentItem(Document document) {
        StageDocumentItemResponse dto = new StageDocumentItemResponse();
        dto.setId(document.getId());
        dto.setTitle(document.getTitle());
        dto.setType(document.getType().name());
        dto.setStatus(document.getStatus().name());
        dto.setFileUrl(document.getFileUrl());
        dto.setPreviewUrl(document.getPreviewUrl());

        dto.setRequired(true); // можно подтягивать из StageDocumentRequirement
        if (document.getCreatedAt() != null) {
            dto.setCreatedAt(document.getCreatedAt().toString());
        }
        if (document.getSignedAt() != null) {
            dto.setSignedAt(document.getSignedAt().toString());
        }

        if (document.getSignedBy() != null) {
            StageDocumentItemResponse.SignedByInfo info =
                    new StageDocumentItemResponse.SignedByInfo();
            info.setId(document.getSignedBy().getId());
            info.setFullName(document.getSignedBy().getFullName());
            info.setEmail(document.getSignedBy().getEmail());
            dto.setSignedBy(info);
        }

        return dto;
    }

    // ---------- Чек-лист документов ----------

    @Override
    @Transactional(readOnly = true)
    public DocumentChecklistResponse getDocumentChecklist(Long objectId, String stage) {
        ConstructionObject object = objectRepository.findById(objectId)
                .orElseThrow(() -> new EntityNotFoundException("Construction object not found"));

        HighLevelStage highLevelStage = HighLevelStage.valueOf(stage);

        List<StageDocumentRequirement> requirements =
                requirementRepository.findByHighLevelStageOrderByOrderIndex(highLevelStage);

        List<Document> documents = documentRepository.findByConstructionObjectId(objectId);

        List<DocumentChecklistItemResponse> checklist = requirements.stream()
                .map(req -> {
                    Document doc = documents.stream()
                            .filter(d -> d.getType() == req.getDocumentType())
                            .findFirst()
                            .orElse(null);

                    DocumentChecklistItemResponse item = new DocumentChecklistItemResponse();
                    item.setId(req.getId());
                    item.setDocumentType(req.getDocumentType().name());
                    item.setTitle(generateTitle(req));
                    item.setRequired(req.isRequired());
                    item.setOrder(req.getOrderIndex());

                    if (doc != null) {
                        item.setDocumentStatus(doc.getStatus().name());
                        item.setDocumentId(doc.getId());
                        item.setSigned(doc.getStatus() == DocumentStatus.SIGNED);
                    } else {
                        item.setDocumentStatus(null);
                        item.setDocumentId(null);
                        item.setSigned(false);
                    }

                    return item;
                })
                .collect(Collectors.toList());

        long totalRequired = checklist.stream()
                .filter(DocumentChecklistItemResponse::isRequired)
                .count();

        long totalSigned = checklist.stream()
                .filter(i -> i.isRequired() && i.isSigned())
                .count();

        DocumentChecklistResponse response = new DocumentChecklistResponse();
        response.setObjectId(object.getId());
        response.setStage(stage);
        response.setChecklist(checklist);
        response.setTotalRequired((int) totalRequired);
        response.setTotalSigned((int) totalSigned);
        response.setAllRequiredSigned(totalRequired > 0 && totalRequired == totalSigned);

        return response;
    }

    // ---------- Детали документа, подпись, отклонение ----------

    @Override
    @Transactional(readOnly = true)
    public DocumentDetailsResponse getDocument(Long documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new EntityNotFoundException("Document not found"));
        return toDetailsResponse(document);
    }

    @Override
    public DocumentDetailsResponse signDocument(Long documentId, DocumentCommentRequest body) {
        UserAccount current = userService.getCurrentUserAccount();
        if (current.getRole() != UserRole.CUSTOMER) {
            throw new AccessDeniedException("Only customer can sign document");
        }

        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new EntityNotFoundException("Document not found"));

        document.setStatus(DocumentStatus.SIGNED);
        document.setSignedAt(OffsetDateTime.now());
        document.setSignedBy(current);

        document = documentRepository.save(document);
        saveHistory(document, current, DocumentHistoryAction.SIGNED,
                body != null ? body.getComment() : null);
        return toDetailsResponse(document);
    }

    @Override
    public DocumentDetailsResponse rejectDocument(Long documentId, DocumentRejectRequest body) {
        UserAccount current = userService.getCurrentUserAccount();
        if (current.getRole() != UserRole.CUSTOMER) {
            throw new AccessDeniedException("Only customer can reject document");
        }

        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new EntityNotFoundException("Document not found"));

        document.setStatus(DocumentStatus.REJECTED);
        document.setSignedAt(null);
        document.setSignedBy(null);

        document.setRejectedAt(OffsetDateTime.now());
        document.setRejectedBy(current);
        document.setRejectionReason(body.getReason());

        document = documentRepository.save(document);
        saveHistory(document, current, DocumentHistoryAction.REJECTED,
                body != null ? body.getReason() : null);
        return toDetailsResponse(document);
    }

    private DocumentDetailsResponse toDetailsResponse(Document document) {
        DocumentDetailsResponse dto = new DocumentDetailsResponse();
        dto.setId(document.getId());
        dto.setTitle(document.getTitle());
        dto.setType(document.getType().name());
        dto.setStatus(document.getStatus().name());

        if (document.getStage() != null) {
            dto.setHighLevelStage(
                    mapStageTypeToHighLevel(document.getStage().getType()).name()
            );
            dto.setStageId(document.getStage().getId());
            dto.setStageName(document.getStage().getType().name());
        }

        dto.setRequired(true); // можно вычислять по StageDocumentRequirement
        if (document.getCreatedAt() != null) {
            dto.setCreatedAt(document.getCreatedAt().toString());
        }

        dto.setFileUrl(document.getFileUrl());
        dto.setPreviewUrl(document.getPreviewUrl());

        if (document.getConstructionObject() != null) {
            dto.setObjectId(document.getConstructionObject().getId());
        }
        if (document.getSignedAt() != null) {
            dto.setSignedAt(document.getSignedAt().toString());
        }
        if (document.getSignedBy() != null) {
            DocumentDetailsResponse.SignedByInfo info =
                    new DocumentDetailsResponse.SignedByInfo();
            info.setId(document.getSignedBy().getId());
            info.setFullName(document.getSignedBy().getFullName());
            info.setEmail(document.getSignedBy().getEmail());
            dto.setSignedBy(info);
        }
        if (document.getRejectedAt() != null) {
            dto.setRejectedAt(document.getRejectedAt().toString());
        }
        if (document.getRejectedBy() != null) {
            DocumentDetailsResponse.RejectedByInfo r =
                    new DocumentDetailsResponse.RejectedByInfo();
            r.setId(document.getRejectedBy().getId());
            r.setFullName(document.getRejectedBy().getFullName());
            dto.setRejectedBy(r);
        }
        dto.setRejectionReason(document.getRejectionReason());

        return dto;
    }

    // ---------- Генерация обязательных документов ----------

    @Override
    public void createRequiredDocumentsForObject(ConstructionObject object) {
        List<ConstructionStage> stages =
                stageRepository.findByConstructionObjectId(object.getId());

        for (ConstructionStage stage : stages) {
            HighLevelStage highLevelStage = mapStageTypeToHighLevel(stage.getType());

            List<StageDocumentRequirement> requirements =
                    requirementRepository.findByHighLevelStageOrderByOrderIndex(highLevelStage);

            for (StageDocumentRequirement req : requirements) {
                boolean exists = documentRepository
                        .existsByStageIdAndType(stage.getId(), req.getDocumentType());
                if (exists) {
                    continue;
                }

                Document doc = new Document();
                doc.setStage(stage);
                doc.setConstructionObject(object);
                doc.setTitle(generateTitle(req));
                doc.setType(req.getDocumentType());
                doc.setStatus(DocumentStatus.DRAFT);
                doc.setCreatedAt(OffsetDateTime.now());
                documentRepository.save(doc);
                saveHistory(doc, null, DocumentHistoryAction.CREATED, null);
            }
        }
    }

    private HighLevelStage mapStageTypeToHighLevel(StageType type) {
        return switch (type) {
            case PREPARATION -> HighLevelStage.DOCS_APPROVAL;
            case HANDOVER -> HighLevelStage.COMPLETION;
            default -> HighLevelStage.BUILDING;
        };
    }

    private String generateTitle(StageDocumentRequirement req) {
        return switch (req.getDocumentType()) {
            case CONTRACT -> "Договор подряда";
            case ESTIMATE -> "Смета";
            case PROJECT_PLAN -> "Проектная документация";
            case BUILDING_PERMIT -> "Разрешение на строительство";
            case STAGE_REPORT -> "Отчёт по этапу";
            case ADDITIONAL_AGREEMENT -> "Дополнительное соглашение";
            case FINAL_ACCEPTANCE_ACT -> "Акт приёмки";
            case FINAL_REPORT -> "Итоговый отчёт";
            case WARRANTY -> "Гарантия";
            default -> req.getDocumentType().name();
        };
    }

    private void saveHistory(Document document,
                             UserAccount actor,
                             DocumentHistoryAction action,
                             String comment) {
        DocumentHistory h = new DocumentHistory();
        h.setDocument(document);
        h.setActor(actor);
        h.setAction(action);
        h.setTimestamp(OffsetDateTime.now());
        h.setComment(comment);
        historyRepository.save(h);
    }

    @Transactional(readOnly = true)
    public DocumentHistoryResponse getDocumentHistory(Long documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new EntityNotFoundException("Document not found"));

        var events = historyRepository.findByDocumentIdOrderByTimestampDesc(document.getId());

        List<DocumentHistoryItemResponse> items = events.stream().map(e -> {
            DocumentHistoryItemResponse dto = new DocumentHistoryItemResponse();
            dto.setAction(e.getAction().name());
            dto.setTimestamp(e.getTimestamp().toString());
            dto.setComment(e.getComment());

            if (e.getActor() != null) {
                DocumentHistoryItemResponse.ActorInfo actor = new DocumentHistoryItemResponse.ActorInfo();
                actor.setId(e.getActor().getId());
                actor.setFullName(e.getActor().getFullName());
                actor.setEmail(e.getActor().getEmail());
                dto.setActor(actor);
            }
            return dto;
        }).toList();

        DocumentHistoryResponse resp = new DocumentHistoryResponse();
        resp.setDocumentId(document.getId());
        resp.setHistory(items);
        return resp;
    }
}
