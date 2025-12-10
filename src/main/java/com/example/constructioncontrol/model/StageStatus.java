package com.example.constructioncontrol.model;

public enum StageStatus {
    // Этап ещё не начался
    NOT_STARTED,
    // Работы выполняются
    IN_PROGRESS,
    // Этап завершён
    COMPLETED,
    // Есть замечание по этапу
    NEEDS_ATTENTION
}
