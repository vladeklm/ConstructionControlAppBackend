package com.example.constructioncontrol.dto.document;

import lombok.Data;

@Data
public class StageDocumentItemResponse {
    private Long id;
    private String title;
    private String type;
    private String status;
    private String fileUrl;
    private String previewUrl;

    private boolean required;
    private String createdAt;
    private String signedAt;

    private SignedByInfo signedBy;

    @Data
    public static class SignedByInfo {
        private Long id;
        private String fullName;
        private String email;
    }
}
