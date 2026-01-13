package com.example.constructioncontrol.dto;

import com.example.constructioncontrol.model.MaterialType;
import java.math.BigDecimal;

public record ProjectTemplateFilter(
        BigDecimal areaMin,
        BigDecimal areaMax,
        Integer floors,
        BigDecimal priceMin,
        BigDecimal priceMax,
        MaterialType materials
) {
}
