package com.example.constructioncontrol.model;

public enum StageType {
    // Подготовительный этап: выбор участка, проектирование, разрешения
    PREPARATION,
    // Земляные работы и фундамент
    FOUNDATION,
    // Стены и перекрытия
    WALLS,
    // Монтаж крыши
    ROOFING,
    // Установка окон и дверей
    WINDOWS_AND_DOORS,
    // Наружная отделка и утепление
    FACADE,
    // Инженерные коммуникации (вода, канализация, электричество, отопление, вентиляция)
    ENGINEERING_SYSTEMS,
    // Финишная внутренняя отделка
    INTERIOR_FINISHING,
    // Обустройство придомовой территории
    LANDSCAPING,
    // Приемка и сдача объекта
    HANDOVER
}
