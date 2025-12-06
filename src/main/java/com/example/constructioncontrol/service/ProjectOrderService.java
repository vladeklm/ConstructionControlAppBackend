package com.example.constructioncontrol.service;

import com.example.constructioncontrol.dto.CreateOrderRequest;
import com.example.constructioncontrol.dto.ProjectOrderResponse;

import java.util.List;

public interface ProjectOrderService {

    ProjectOrderResponse createOrder(CreateOrderRequest request);

    List<ProjectOrderResponse> getAllOrders();

    ProjectOrderResponse getOrder(Long id);
}
