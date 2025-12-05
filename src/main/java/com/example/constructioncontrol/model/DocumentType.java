package com.example.constructioncontrol.model;

public enum DocumentType {
    // Договор на строительство
    CONTRACT,
    // Смета
    ESTIMATE,
    // Архитектурный/конструктивный проект
    PROJECT_PLAN,
    // Разрешение на строительство
    BUILDING_PERMIT,
    // Документ, прикреплённый к этапу (отчёт)
    STAGE_REPORT,
    // Допсоглашения по изменению условий
    ADDITIONAL_AGREEMENT,
    // Финальный акт приёмки
    FINAL_ACCEPTANCE_ACT,
    // Итоговый отчёт
    FINAL_REPORT,
    // Гарантийные обязательства
    WARRANTY
}
