package com.example.constructioncontrol.repository.specification;

import com.example.constructioncontrol.model.ProjectTemplate;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ProjectTemplateSpecifications {

    private ProjectTemplateSpecifications() {
    }

    public static Specification<ProjectTemplate> filterBy(BigDecimal areaMin,
                                                          BigDecimal areaMax,
                                                          Integer floors) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (Objects.nonNull(areaMin)) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("totalArea"), areaMin));
            }
            if (Objects.nonNull(areaMax)) {
                predicates.add(cb.lessThanOrEqualTo(root.get("totalArea"), areaMax));
            }
            if (Objects.nonNull(floors)) {
                predicates.add(cb.equal(root.get("floors"), floors));
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }
}

