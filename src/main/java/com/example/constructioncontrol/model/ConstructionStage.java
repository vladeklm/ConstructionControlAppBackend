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
    private ConstructionStageStatus status = ConstructionStageStatus.NOT_STARTED;

    private Integer progressPercentage = 0;
    private Integer orderIndex;

    private LocalDate plannedStartDate;
    private LocalDate plannedEndDate;
    private LocalDate actualStartDate;
    private LocalDate actualEndDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "construction_object_id")
    private ConstructionObject constructionObject;

    @OneToMany(mappedBy = "stage", fetch = FetchType.LAZY)
    private List<StageReport> reports = new ArrayList<>();

    @OneToMany(mappedBy = "stage", fetch = FetchType.LAZY)
    private List<Document> documents = new ArrayList<>();
}