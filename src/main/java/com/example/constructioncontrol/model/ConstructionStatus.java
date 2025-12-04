package com.example.constructioncontrol.model;

public enum ConstructionStatus {
    // Объект только в подготовке документов
    DOCUMENT_PREPARATION,
    // Идёт стройка, доступны отчёты/камеры/чат
    UNDER_CONSTRUCTION,
    // Стройка завершена, ожидаем финальное подписание
    READY_FOR_FINAL_SIGNATURE,
    // Все финальные документы подписаны, объект сдан
    COMPLETED
}
