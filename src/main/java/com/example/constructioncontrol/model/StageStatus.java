package com.example.constructioncontrol.model;

public enum StageStatus {
    // Этап ещё не начался
    NOT_STARTED,
    // На согласовании
    WAITING_FOR_ACCEPT,
    // Работы выполняются
    IN_PROGRESS,
    // Этап завершён
    COMPLETED
}
