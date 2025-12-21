package com.example.constructioncontrol.controller;

import com.example.constructioncontrol.dto.ProjectTemplateCreateRequest;
import com.example.constructioncontrol.dto.ProjectTemplateFilter;
import com.example.constructioncontrol.dto.ProjectTemplateListItemResponse;
import com.example.constructioncontrol.dto.ProjectTemplateResponse;
import com.example.constructioncontrol.service.ProjectTemplateService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectTemplateController {

    private final ProjectTemplateService projectTemplateService;

    public ProjectTemplateController(ProjectTemplateService projectTemplateService) {
        this.projectTemplateService = projectTemplateService;
    }

    @GetMapping
    public List<ProjectTemplateListItemResponse> getProjects(
            @RequestParam(value = "areaMin", required = false) BigDecimal areaMin,
            @RequestParam(value = "areaMax", required = false) BigDecimal areaMax,
            @RequestParam(value = "floors", required = false) Integer floors,
            @RequestParam(value = "priceMin", required = false) BigDecimal priceMin,
            @RequestParam(value = "priceMax", required = false) BigDecimal priceMax,
            @RequestParam(value = "materials", required = false) String materials
    ) {
        return projectTemplateService.findAll(new ProjectTemplateFilter(areaMin, areaMax, floors, priceMin, priceMax, materials));
    }

    @GetMapping("/{id}")
    public ProjectTemplateResponse getProject(@PathVariable Long id) {
        return projectTemplateService.findById(id);
    }

    @PostMapping
    public ProjectTemplateResponse createProject(@Valid @RequestBody ProjectTemplateCreateRequest request) {
        return projectTemplateService.create(request);
    }
}
