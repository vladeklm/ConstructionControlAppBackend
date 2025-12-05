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

// Фото внутри отчёта по этапу
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "stageReport")
@Entity
@Table(name = "stage_photos")
public class StagePhoto extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stage_report_id")
    private StageReport stageReport; // Отчёт, к которому относится фото

    @Column(nullable = false)
    private String url; // Ссылка на изображение

    private String caption; // Подпись к фото
}
