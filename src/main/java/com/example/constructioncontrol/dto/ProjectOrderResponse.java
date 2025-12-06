package com.example.constructioncontrol.dto;

import lombok.Data;

@Data
public class ProjectOrderResponse {
    private Long id;
    private String address;
    private String status;
    private Long constructionObjectId;
    private Long projectTemplateId;
}
