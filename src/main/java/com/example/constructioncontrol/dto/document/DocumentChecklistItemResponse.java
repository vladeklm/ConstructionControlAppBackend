package com.example.constructioncontrol.dto.document;

import lombok.Data;

@Data
public class DocumentChecklistItemResponse {

    private Long id;
    private String documentType;
    private String title;
    private boolean required;
    private Integer order;

    private String documentStatus;
    private Long documentId;
    private boolean signed;
}