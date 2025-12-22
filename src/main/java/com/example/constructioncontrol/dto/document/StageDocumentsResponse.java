package com.example.constructioncontrol.dto.document;

import java.util.List;
import lombok.Data;

@Data
public class StageDocumentsResponse {
    private Long stageId;
    private String stageName;
    private Long constructionObjectId;
    private String objectAddress;
    private String highLevelStage;
    private List<StageDocumentItemResponse> documents;
}