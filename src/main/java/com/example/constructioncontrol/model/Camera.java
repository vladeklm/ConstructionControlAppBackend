package com.example.constructioncontrol.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

// Камера живого эфира по объекту
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "constructionObject")
@Entity
@Table(name = "cameras")
public class Camera extends BaseEntity {

    @Column(nullable = false)
    private String name; // Отображаемое имя: «Камера 1»

    @Column(nullable = false)
    private String streamUrl; // RTSP/HLS для плеера

    private String previewUrl; // Превью на карточке камеры
    private boolean active = true; // Флаг доступности

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "construction_object_id")
    private ConstructionObject constructionObject; // Объект, к которому относится камера
}
