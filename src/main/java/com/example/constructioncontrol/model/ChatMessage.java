package com.example.constructioncontrol.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

// Сообщение в чате: текст/фото/ссылки на документы и отчёты
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"chatThread", "sender"})
@Entity
@Table(name = "chat_messages")
public class ChatMessage extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_thread_id")
    private ChatThread chatThread; // Чат, к которому принадлежит сообщение

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private UserAccount sender; // Кто отправил (Менеджер/Инженер/Вы)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType type = MessageType.TEXT; // Тип сообщения (текст/фото/ссылки)

    @Column(length = 3000)
    private String content; // Текст сообщения

    private String attachmentUrl; // Ссылка на вложение (фото)

    private OffsetDateTime sentAt; // Время отправки

}
