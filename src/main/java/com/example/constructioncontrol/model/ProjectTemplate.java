package com.example.constructioncontrol.model;

import jakarta.persistence.Column;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

// Типовой проект из каталога
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "media")
@Entity
@Table(name = "project_templates")
public class ProjectTemplate extends BaseEntity {

    @Column(nullable = false)
    private String name; // Название проекта в каталоге

    private BigDecimal totalArea; // Площадь для фильтров/описания

    private Integer floors; // Кол-во этажей для фильтров

    private BigDecimal basePrice; // Базовая цена на карточке проекта

    private String mainMaterials; // Ключевые материалы для описания

    @Column(length = 2000)
    private String description; // Краткое описание

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "project_template_default_stages", joinColumns = @JoinColumn(name = "project_template_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "stage")
    private List<StageType> defaultStages = new ArrayList<>(); // Шаблон этапов стройки

    @OneToMany(mappedBy = "projectTemplate", fetch = FetchType.LAZY)
    private List<ProjectMedia> media = new ArrayList<>(); // Рендеры/фото/планы для показа проекта
}
