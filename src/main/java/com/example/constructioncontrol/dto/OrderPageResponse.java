package com.example.constructioncontrol.dto;

import java.util.List;

public class OrderPageResponse {

    private List<OrderListItemResponse> content;
    private long totalElements;
    private int totalPages;
    private int currentPage;

    public OrderPageResponse() {
    }

    public List<OrderListItemResponse> getContent() {
        return content;
    }

    public void setContent(List<OrderListItemResponse> content) {
        this.content = content;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }
}
