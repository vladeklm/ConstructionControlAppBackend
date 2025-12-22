package com.example.constructioncontrol.dto.document;

import lombok.Data;

@Data
public class DocumentHistoryItemResponse {
    private String action;      // CREATED, SIGNED, REJECTED
    private String timestamp;   // ISO-строка
    private String comment;
    private ActorInfo actor;

    @Data
    public static class ActorInfo {
        private Long id;
        private String fullName;
        private String email;
    }
}