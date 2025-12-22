package com.example.constructioncontrol.controller;

import com.example.constructioncontrol.dto.document.*;
import com.example.constructioncontrol.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    // 1) Все документы конкретной стадии
    @GetMapping("/stages/{stageId}/documents")
    public StageDocumentsResponse getStageDocuments(@PathVariable Long stageId) {
        return documentService.getStageDocuments(stageId);
    }

    // 2) Чек-лист документов по объекту и этапу жизненного цикла
    @GetMapping("/objects/{objectId}/document-checklist")
    public DocumentChecklistResponse getChecklist(
            @PathVariable Long objectId,
            @RequestParam String stage) {
        return documentService.getDocumentChecklist(objectId, stage);
    }

    // 3) Один документ
    @GetMapping("/documents/{documentId}")
    public DocumentDetailsResponse getDocument(@PathVariable Long documentId) {
        return documentService.getDocument(documentId);
    }

    // 4) Подписать документ (роль CUSTOMER)
    @PostMapping("/documents/{documentId}/sign")
    public DocumentDetailsResponse signDocument(
            @PathVariable Long documentId,
            @RequestBody(required = false) DocumentCommentRequest body) {
        return documentService.signDocument(documentId, body);
    }

    // 5) Отклонить документ (роль CUSTOMER)
    @PostMapping("/documents/{documentId}/reject")
    public DocumentDetailsResponse rejectDocument(
            @PathVariable Long documentId,
            @RequestBody DocumentRejectRequest body) {
        return documentService.rejectDocument(documentId, body);
    }

    //6 Сохранение истории
    @GetMapping("/documents/{documentId}/history")
    public DocumentHistoryResponse getDocumentHistory(@PathVariable Long documentId) {
        return documentService.getDocumentHistory(documentId);
    }
}
