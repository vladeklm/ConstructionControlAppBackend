package com.example.constructioncontrol.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;

// Базовый класс с id и метаданными создания/обновления
@Getter
@Setter
@MappedSuperclass
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // первичный ключ для всех сущностей

    private OffsetDateTime createdAt; // для аудита времени создания
    private OffsetDateTime updatedAt; // для аудита изменений

    @PrePersist
    protected void onCreate() { // Проставляем временные метки при создании
        OffsetDateTime now = OffsetDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() { // Обновляем время изменения
        this.updatedAt = OffsetDateTime.now();
    }
}
