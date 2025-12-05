package com.example.constructioncontrol.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

// Чат по объекту
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"constructionObject", "messages", "participants"})
@Entity
@Table(name = "chat_threads")
public class ChatThread extends BaseEntity {

    private String topic; // Тема чата, можно использовать адрес/этап

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "construction_object_id")
    private ConstructionObject constructionObject; // К какому объекту чат

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "chat_participants",
            joinColumns = @JoinColumn(name = "chat_thread_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<UserAccount> participants = new HashSet<>(); // Участники (клиент, менеджер, инженер)

    @OneToMany(mappedBy = "chatThread", fetch = FetchType.LAZY)
    private List<ChatMessage> messages = new ArrayList<>(); // Сообщения в чате
}
