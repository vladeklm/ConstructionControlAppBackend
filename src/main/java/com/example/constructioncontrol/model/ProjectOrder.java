package com.example.constructioncontrol.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

// Заявка на создание объекта
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"projectTemplate", "constructionObject", "customer"})
@Entity
@Table(name = "project_orders")
public class ProjectOrder extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_template_id")
    private ProjectTemplate projectTemplate; // Выбранный типовой проект

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private UserAccount customer; // Пользователь, оставивший заявку

    @Column(nullable = false)
    private String address; // Адрес участка из формы

    private String requestedTimeline; // Пожелания по срокам

    private String phone;
    private String email; // Контакты, если нет авторизации

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.SUBMITTED; // Статус обработки заявки

    private OffsetDateTime submittedAt; // Когда отправили
    private OffsetDateTime processedAt; // Когда обработали менеджеры

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "construction_object_id")
    private ConstructionObject constructionObject; // Ссылка на созданный объект после одобрения
}
