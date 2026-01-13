package com.example.constructioncontrol.dto;

import com.example.constructioncontrol.model.MaterialType;
import com.example.constructioncontrol.model.StageType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Value;

import java.math.BigDecimal;
import java.util.List;

@Value
public class ProjectTemplateCreateRequest {
    @NotBlank
    String name;
    @Positive
    BigDecimal totalArea;
    @Positive
    Integer floors;
    @Positive
    BigDecimal basePrice;
    @NotNull
    MaterialType mainMaterials;
    String description;
    List<StageType> defaultStages;
    List<ProjectMediaRequest> media;
}
