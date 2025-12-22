package com.example.constructioncontrol.repository;

import com.example.constructioncontrol.model.OrderStatus;
import com.example.constructioncontrol.model.ProjectOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectOrderRepository extends JpaRepository<ProjectOrder, Long> {

    Page<ProjectOrder> findByCustomerId(Long customerId, Pageable pageable);

    Page<ProjectOrder> findByCustomerIdAndStatus(
            Long customerId,
            OrderStatus status,
            Pageable pageable
    );

    Optional<ProjectOrder> findByConstructionObjectId(Long constructionObjectId);
}