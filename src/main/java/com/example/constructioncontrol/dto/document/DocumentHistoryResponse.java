package com.example.constructioncontrol.dto.document;

import lombok.Data;
import java.util.List;

@Data
public class DocumentHistoryResponse {
    private Long documentId;
    private List<DocumentHistoryItemResponse> history;
}