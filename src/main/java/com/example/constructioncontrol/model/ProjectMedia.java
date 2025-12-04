package com.example.constructioncontrol.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

// Медиа для показа карточки типового проекта
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "projectTemplate")
@Entity
@Table(name = "project_media")
public class ProjectMedia extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_template_id")
    private ProjectTemplate projectTemplate; // Проект, к которому относится медиа

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProjectMediaType type; // Тип: рендер/фото/план

    @Column(nullable = false)
    private String url; // Ссылка на файл для фронта

    private Integer sortOrder; // Порядок отображения в галерее
}
