package com.example.constructioncontrol.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

// Отчёт по этапу
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"stage", "author", "photos"})
@Entity
@Table(name = "stage_reports")
public class StageReport extends BaseEntity {

    @Column(nullable = false)
    private String title; // Заголовок отчёта, например «Этап: Фундамент»

    private LocalDate reportDate; // Дата отчёта для сортировки и отображения

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StageReportStatus status = StageReportStatus.OK; // Индикатор «ОК/замечание»

    @Column(length = 3000)
    private String comment; // Комментарий инженера

    private String pdfUrl; // Ссылка на PDF отчёта, если есть

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private UserAccount author; // Кто создал отчёт (инженер)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stage_id")
    private ConstructionStage stage; // Какому этапу принадлежит отчёт

    @OneToMany(mappedBy = "stageReport", fetch = FetchType.LAZY)
    private List<StagePhoto> photos = new ArrayList<>(); // Фотоотчёт (галерея)
}
