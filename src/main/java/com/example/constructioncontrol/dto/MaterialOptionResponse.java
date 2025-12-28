package com.example.constructioncontrol.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MaterialOptionResponse {
    private String code;   // Значение enum для фильтра/запроса
    private String name;   // Человекочитаемое название на русском
}
