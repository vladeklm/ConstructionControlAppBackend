package com.example.constructioncontrol.service;

import com.example.constructioncontrol.dto.CreateOrderRequest;
import com.example.constructioncontrol.dto.OrderPageResponse;
import com.example.constructioncontrol.dto.ProjectOrderResponse;
import com.example.constructioncontrol.model.OrderStatus;

import java.util.List;

public interface ProjectOrderService {

    ProjectOrderResponse createOrder(CreateOrderRequest request);

    OrderPageResponse getOrdersForCurrentUser(OrderStatus status, int page, int size);

    ProjectOrderResponse getOrderForCurrentUser(Long orderId);

}