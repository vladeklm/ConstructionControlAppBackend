package com.example.constructioncontrol.controller;

import com.example.constructioncontrol.dto.CreateOrderRequest;
import com.example.constructioncontrol.dto.OrderPageResponse;
import com.example.constructioncontrol.dto.ProjectOrderResponse;
import com.example.constructioncontrol.model.OrderStatus;
import com.example.constructioncontrol.service.ProjectOrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@PreAuthorize("hasRole('CUSTOMER')")
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
    public OrderPageResponse getOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) OrderStatus status
    ) {
        return projectOrderService.getOrdersForCurrentUser(status, page, size);
    }

    @GetMapping("/{orderId}")
    public ProjectOrderResponse getOrderById(@PathVariable Long orderId) {
        return projectOrderService.getOrderForCurrentUser(orderId);
    }
}