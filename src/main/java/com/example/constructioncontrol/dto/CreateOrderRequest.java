package com.example.constructioncontrol.dto;

import lombok.Data;

@Data
public class CreateOrderRequest {
    private Long projectTemplateId;
    private String address;
    private String requestedTimeline;
    private String phone;
    private String email;
    private Long customerId; // если клиент авторизован
}
