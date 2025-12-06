package com.example.constructioncontrol.repository;

import com.example.constructioncontrol.model.ProjectOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectOrderRepository extends JpaRepository<ProjectOrder, Long> {
}
