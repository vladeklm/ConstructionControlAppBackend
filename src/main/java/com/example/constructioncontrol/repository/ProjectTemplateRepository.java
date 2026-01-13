package com.example.constructioncontrol.repository;

import com.example.constructioncontrol.model.ProjectTemplate;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProjectTemplateRepository extends JpaRepository<ProjectTemplate, Long>,
        JpaSpecificationExecutor<ProjectTemplate> {

    @Override
    @EntityGraph(attributePaths = {"media"})
    List<ProjectTemplate> findAll();

    @EntityGraph(attributePaths = {"media"})
    List<ProjectTemplate> findAll(org.springframework.data.jpa.domain.Specification<ProjectTemplate> spec,
                                  org.springframework.data.domain.Sort sort);

    @Override
    @EntityGraph(attributePaths = {"media"})
    Optional<ProjectTemplate> findById(Long id);

    interface RangesProjection {
        BigDecimal getMinArea();
        BigDecimal getMaxArea();
        BigDecimal getMinPrice();
        BigDecimal getMaxPrice();
    }

    @org.springframework.data.jpa.repository.Query(
            "select min(p.totalArea) as minArea, max(p.totalArea) as maxArea, " +
                    "min(p.basePrice) as minPrice, max(p.basePrice) as maxPrice " +
                    "from ProjectTemplate p")
    RangesProjection findRanges();

}
