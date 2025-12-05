package com.example.constructioncontrol.model;

public enum OrderStatus {
    // Заявка отправлена
    SUBMITTED,
    // Менеджер рассматривает/связывается
    IN_REVIEW,
    // Заявка принята к работе
    APPROVED,
    // Отклонена
    DECLINED,
    // Переведена в объект строительства
    CONVERTED_TO_OBJECT
}
