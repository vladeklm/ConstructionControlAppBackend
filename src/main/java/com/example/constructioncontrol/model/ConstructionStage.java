package com.example.constructioncontrol.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

// Этап строительства для блока прогресса на экране обзора
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"constructionObject", "reports", "documents"})
@Entity
@Table(name = "construction_stages")
public class ConstructionStage extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StageType type; // Тип этапа (фундамент, стены и т.д.)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StageStatus status = StageStatus.NOT_STARTED; // Для подсветки текущего этапа

    private Integer progressPercentage = 0; // Линейка прогресса по этапам
    private Integer orderIndex; // Порядок отображения

    private LocalDate plannedStartDate;
    private LocalDate plannedEndDate;
    private LocalDate actualStartDate; // Фактическое начало этапа
    private LocalDate actualEndDate; // Фактическое завершение этапа

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "construction_object_id")
    private ConstructionObject constructionObject; // Объект, к которому относится этап

    @OneToMany(mappedBy = "stage", fetch = FetchType.LAZY)
    private List<StageReport> reports = new ArrayList<>(); // Отчёты инженера по этапу

    @OneToMany(mappedBy = "stage", fetch = FetchType.LAZY)
    private List<Document> documents = new ArrayList<>(); // Документы, привязанные к этапу
}
