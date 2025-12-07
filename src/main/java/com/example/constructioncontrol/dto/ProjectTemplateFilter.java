package com.example.constructioncontrol.dto;

import java.math.BigDecimal;

public record ProjectTemplateFilter(BigDecimal areaMin, BigDecimal areaMax, Integer floors) {
}

