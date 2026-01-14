package com.example.constructioncontrol.controller;

import com.example.constructioncontrol.repository.CameraRepository; // Убедитесь, что пакет правильный
import com.example.constructioncontrol.model.Camera; // Импорт вашей сущности Camera
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/cameras")
@CrossOrigin(origins = "*")
public class CameraController {

    private final CameraRepository cameraRepository;

    @Autowired
    public CameraController(CameraRepository cameraRepository) {
        this.cameraRepository = cameraRepository;
    }

    @GetMapping("/{constructionObjectId}/stream-url")
    public Map<String, String> getStreamUrl(@PathVariable Long constructionObjectId) {

        Camera camera = cameraRepository.findByConstructionObjectId(constructionObjectId);

        if (camera == null) {
            //throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Камера для объекта с ID " + constructionObjectId + " не найдена");
            camera = new Camera();
            camera.setStreamUrl("http://localhost:8889/cam1");
        }

        String token = UUID.randomUUID().toString();


        String cameraStreamUrl = camera.getStreamUrl();

        String whepUrl = String.format("%s/whep?token=%s", cameraStreamUrl, token);

        return Map.of(
                "url", whepUrl,
                "protocol", "whep"
        );
    }
}