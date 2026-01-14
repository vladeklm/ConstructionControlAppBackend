package com.example.constructioncontrol.repository;

import com.example.constructioncontrol.model.Camera;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CameraRepository extends JpaRepository<Camera, Long> {

    Camera findByConstructionObjectId(Long constructionObjectId);
}
