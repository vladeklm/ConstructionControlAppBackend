package com.example.constructioncontrol.dto.document;

import lombok.Data;

@Data
public class DocumentDetailsResponse {
    private Long id;
    private String title;
    private String type;
    private String status;

    private String highLevelStage;   // DOCS_APPROVAL, BUILDING, COMPLETION
    private boolean required;        // обязательный ли документ
    private String createdAt;        // дата создания

    private String fileUrl;
    private String previewUrl;

    private Long objectId;           // id объекта
    private Long stageId;
    private String stageName;        // PREPARATION, FOUNDATION

    private String signedAt;
    private SignedByInfo signedBy;   // кто подписал

    @Data
    public static class SignedByInfo {
        private Long id;
        private String fullName;
        private String email;
    }
    private String rejectedAt;
    private RejectedByInfo rejectedBy;
    private String rejectionReason;

    @Data
    public static class RejectedByInfo {
        private Long id;
        private String fullName;
    }
}
