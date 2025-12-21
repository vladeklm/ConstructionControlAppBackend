package com.example.constructioncontrol.dto;

import com.example.constructioncontrol.model.ProjectMediaType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
public class ProjectMediaRequest {
    @NotNull
    ProjectMediaType type;
    @NotBlank
    String url;
    Integer sortOrder;
}

