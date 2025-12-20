package com.example.constructioncontrol.dto;

import java.time.OffsetDateTime;

public class WebSocketMessageDTO {

    private Long id;
    private Long orderId;
    private Long senderId;
    private String senderName;
    private String content;
    private String type;
    private OffsetDateTime createdAt;

    public WebSocketMessageDTO() {
    }

    public WebSocketMessageDTO(Long id, Long orderId, Long senderId, String senderName, 
                              String content, String type, OffsetDateTime createdAt) {
        this.id = id;
        this.orderId = orderId;
        this.senderId = senderId;
        this.senderName = senderName;
        this.content = content;
        this.type = type;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

