package com.example.constructioncontrol.dto;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class ProjectTemplateListItemResponse {
    Long id;
    String name;
    String previewImageUrl;
    BigDecimal basePrice;
    BigDecimal totalArea;
    Integer floors;
    String mainMaterials;
}
