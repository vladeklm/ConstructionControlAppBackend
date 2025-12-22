package com.example.constructioncontrol.service;

import com.example.constructioncontrol.dto.document.*;
import com.example.constructioncontrol.model.ConstructionObject;
import com.example.constructioncontrol.dto.document.DocumentHistoryResponse;

public interface DocumentService {

    StageDocumentsResponse getStageDocuments(Long stageId);

    DocumentChecklistResponse getDocumentChecklist(Long objectId, String checklistStage);

    DocumentDetailsResponse getDocument(Long documentId);

    DocumentDetailsResponse signDocument(Long documentId, DocumentCommentRequest body);

    DocumentDetailsResponse rejectDocument(Long documentId, DocumentRejectRequest body);

    void createRequiredDocumentsForObject(ConstructionObject object);

    DocumentHistoryResponse getDocumentHistory(Long documentId);
}
