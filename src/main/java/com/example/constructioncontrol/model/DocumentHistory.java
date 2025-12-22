package com.example.constructioncontrol.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "document_history")
public class DocumentHistory extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id", nullable = false)
    private UserAccount actor;              // кто сделал действие

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentHistoryAction action;   // CREATED, SIGNED, REJECTED...

    @Column(nullable = false)
    private OffsetDateTime timestamp;       // когда

    @Column(length = 1000)
    private String comment;                 // комментарий / причина
}
