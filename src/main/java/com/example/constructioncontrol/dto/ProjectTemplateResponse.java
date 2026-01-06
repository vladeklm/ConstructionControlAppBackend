package com.example.constructioncontrol.dto;

import com.example.constructioncontrol.model.StageType;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.util.List;

@Value
@Builder
public class ProjectTemplateResponse {
    Long id;
    String name;
    BigDecimal totalArea;
    Integer floors;
    BigDecimal basePrice;
    String mainMaterials;
    String description;
    List<StageType> defaultStages;
    List<ProjectMediaResponse> media;
}
