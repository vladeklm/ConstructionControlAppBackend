package com.example.constructioncontrol.dto;

import java.time.OffsetDateTime;

public class OrderListItemResponse {

    private Long id;
    private String address;
    private String status;

    private String requestedTimeline;
    private String phone;
    private String email;
    private OffsetDateTime submittedAt;

    private Long constructionObjectId;
    private Long projectTemplateId;
    private String projectTemplateName;

    public OrderListItemResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRequestedTimeline() {
        return requestedTimeline;
    }

    public void setRequestedTimeline(String requestedTimeline) {
        this.requestedTimeline = requestedTimeline;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public OffsetDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(OffsetDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public Long getConstructionObjectId() {
        return constructionObjectId;
    }

    public void setConstructionObjectId(Long constructionObjectId) {
        this.constructionObjectId = constructionObjectId;
    }

    public Long getProjectTemplateId() {
        return projectTemplateId;
    }

    public void setProjectTemplateId(Long projectTemplateId) {
        this.projectTemplateId = projectTemplateId;
    }

    public String getProjectTemplateName() {
        return projectTemplateName;
    }

    public void setProjectTemplateName(String projectTemplateName) {
        this.projectTemplateName = projectTemplateName;
    }
}
