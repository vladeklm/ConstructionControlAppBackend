package com.example.constructioncontrol.controller;

import com.example.constructioncontrol.dto.CreateOrderRequest;
import com.example.constructioncontrol.dto.ProjectOrderResponse;
import com.example.constructioncontrol.service.ProjectOrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class ProjectOrderController {

    private final ProjectOrderService projectOrderService;

    public ProjectOrderController(ProjectOrderService projectOrderService) {
        this.projectOrderService = projectOrderService;
    }

    @PostMapping
    public ProjectOrderResponse createOrder(@RequestBody CreateOrderRequest request) {
        return projectOrderService.createOrder(request);
    }

    @GetMapping
    public List<ProjectOrderResponse> getAll() {
        return projectOrderService.getAllOrders();
    }

    @GetMapping("/{id}")
    public ProjectOrderResponse getOne(@PathVariable Long id) {
        return projectOrderService.getOrder(id);
    }
}
