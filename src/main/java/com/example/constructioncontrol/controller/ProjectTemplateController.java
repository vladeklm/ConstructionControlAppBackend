package com.example.constructioncontrol.controller;

import com.example.constructioncontrol.dto.ProjectTemplateFilter;
import com.example.constructioncontrol.dto.ProjectTemplateResponse;
import com.example.constructioncontrol.service.ProjectTemplateService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public List<ProjectTemplateResponse> getProjects(
            @RequestParam(value = "areaMin", required = false) BigDecimal areaMin,
            @RequestParam(value = "areaMax", required = false) BigDecimal areaMax,
            @RequestParam(value = "floors", required = false) Integer floors
    ) {
        return projectTemplateService.findAll(new ProjectTemplateFilter(areaMin, areaMax, floors));
    }

    @GetMapping("/{id}")
    public ProjectTemplateResponse getProject(@PathVariable Long id) {
        return projectTemplateService.findById(id);
    }
}

