package com.example.constructioncontrol.dto;

import java.math.BigDecimal;
import lombok.Value;

@Value
public class ProjectTemplateRangeResponse {
    BigDecimal minArea;
    BigDecimal maxArea;
    BigDecimal minPrice;
    BigDecimal maxPrice;
}

