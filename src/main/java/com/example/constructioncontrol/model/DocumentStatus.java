package com.example.constructioncontrol.model;

public enum DocumentStatus {
    // Черновик до загрузки/отправки
    DRAFT,
    // Ожидает подписи
    AWAITING_SIGNATURE,
    // На согласовании у менеджера/инженера
    IN_REVIEW,
    // Подписан пользователем
    SIGNED,
    // Отклонён, требует обновления
    REJECTED
}
