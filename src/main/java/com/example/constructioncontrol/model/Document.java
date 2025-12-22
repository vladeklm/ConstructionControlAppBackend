package com.example.constructioncontrol.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

// Документ для согласования/подписания
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"constructionObject", "stage", "signedBy", "rejectedBy"})
@Entity
@Table(name = "documents")
public class Document extends BaseEntity {

    @Column(nullable = false)
    private String title; // Заголовок в списке документов

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentType type; // Тип документа (договор, смета, финальный акт)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentStatus status = DocumentStatus.AWAITING_SIGNATURE; // Статус для прогресса

    private String fileUrl;    // Ссылка на PDF для просмотра
    private String previewUrl; // Превью/иконка

    private OffsetDateTime createdAt; // Когда документ был создан

    private OffsetDateTime signedAt;  // Когда подписали

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "signed_by")
    private UserAccount signedBy;     // Кто подписал

    // Отклонение
    private OffsetDateTime rejectedAt; // Когда отклонили

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rejected_by")
    private UserAccount rejectedBy;    // Кто отклонил

    private String rejectionReason;    // Причина отклонения

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "construction_object_id")
    private ConstructionObject constructionObject; // К какому объекту относится документ

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stage_id")
    private ConstructionStage stage; // Для документов по конкретному этапу
}
