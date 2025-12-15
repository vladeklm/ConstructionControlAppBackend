package com.example.constructioncontrol.repository;

import com.example.constructioncontrol.model.ConstructionObject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConstructionObjectRepository extends JpaRepository<ConstructionObject, Long> {
}
