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

// Конкретный объект строительства
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"documents", "stages", "cameras", "selectedProject", "customer"})
@Entity
@Table(name = "construction_objects")
public class ConstructionObject extends BaseEntity {

    @Column(nullable = false)
    private String address; // Адрес объекта

    private String displayName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConstructionStatus status = ConstructionStatus.DOCUMENT_PREPARATION;

    private String contactPhone;

    private String contactEmail; // Контакты клиента, если нет авторизации

    private LocalDate plannedStartDate; // Плановая дата начала работ
    private LocalDate plannedFinishDate; // Плановая дата окончания
    private LocalDate actualFinishDate; //  «Объект сдан»

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_template_id")
    private ProjectTemplate selectedProject; // Какой типовой проект выбран

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private UserAccount customer; // Владелец объекта (заказчик)

    @OneToMany(mappedBy = "constructionObject", fetch = FetchType.LAZY)
    private List<Document> documents = new ArrayList<>(); // Документы по объекту

    @OneToMany(mappedBy = "constructionObject", fetch = FetchType.LAZY)
    private List<ConstructionStage> stages = new ArrayList<>(); // Этапы стройки

    @OneToMany(mappedBy = "constructionObject", fetch = FetchType.LAZY)
    private List<Camera> cameras = new ArrayList<>(); // Камеры живого эфира
}
