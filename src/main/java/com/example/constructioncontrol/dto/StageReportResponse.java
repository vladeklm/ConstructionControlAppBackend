package com.example.constructioncontrol.dto;

import lombok.Data;
import java.time.OffsetDateTime;
import java.util.List;

@Data
public class StageReportResponse {
    private Long id;
    private String title;
    private OffsetDateTime reportDate;
    private String status;
    private String comment;
    private String pdfUrl;
    private AuthorInfo author;
    private List<PhotoInfo> photos;

    @Data
    public static class AuthorInfo {
        private Long id;
        private String fullName;
        private String email;
    }

    @Data
    public static class PhotoInfo {
        private Long id;
        private String photoUrl;
        private String caption;
        private OffsetDateTime uploadedAt;
        private String uploadedBy;
    }
}