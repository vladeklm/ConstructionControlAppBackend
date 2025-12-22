package com.example.constructioncontrol.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Требование по документу для этапа
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "stage_document_requirements")
public class StageDocumentRequirement extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HighLevelStage highLevelStage; // Для какого укрупнённого этапа

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentType documentType; // Какой тип документа требуется

    @Column(nullable = false)
    private boolean required = true; // Обязательный или опциональный

    @Column(nullable = false)
    private int orderIndex = 0; // Порядок отображения в чек-листе UI
}
