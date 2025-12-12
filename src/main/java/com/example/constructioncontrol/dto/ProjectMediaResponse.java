package com.example.constructioncontrol.dto;

import com.example.constructioncontrol.model.ProjectMediaType;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ProjectMediaResponse {
    Long id;
    ProjectMediaType type;
    String url;
    Integer sortOrder;
}

