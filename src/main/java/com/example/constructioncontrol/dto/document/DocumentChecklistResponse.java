package com.example.constructioncontrol.dto.document;

import java.util.List;
import lombok.Data;

@Data
public class DocumentChecklistResponse {
    private Long objectId;
    private String stage;   // например: DOCS_APPROVAL | BUILDING | COMPLETION
    private int totalRequired;
    private int totalSigned;
    private boolean allRequiredSigned;
    private List<DocumentChecklistItemResponse> checklist;
}