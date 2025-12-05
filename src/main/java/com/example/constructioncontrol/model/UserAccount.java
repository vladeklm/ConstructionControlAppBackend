package com.example.constructioncontrol.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

// Пользователь системы: клиент, менеджер, инженер
@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
@Table(name = "users")
public class UserAccount extends BaseEntity {

    @Column(nullable = false)
    private String fullName; // ФИО для отображения в чатах/отчётах

    @Column(unique = true)
    private String email; // для уведомлений/авторизации

    private String phone; // контакт на экране заявки

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role; // роль определяет сценарии (клиент/менеджер/инженер)
}
