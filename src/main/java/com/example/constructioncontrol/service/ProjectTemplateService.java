package com.example.constructioncontrol.service;

import com.example.constructioncontrol.dto.ProjectMediaResponse;
import com.example.constructioncontrol.dto.ProjectTemplateFilter;
import com.example.constructioncontrol.dto.ProjectTemplateResponse;
import com.example.constructioncontrol.model.ProjectMedia;
import com.example.constructioncontrol.model.ProjectTemplate;
import com.example.constructioncontrol.repository.ProjectTemplateRepository;
import com.example.constructioncontrol.repository.specification.ProjectTemplateSpecifications;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@Transactional(readOnly = true)
public class ProjectTemplateService {

    private final ProjectTemplateRepository projectTemplateRepository;

    public ProjectTemplateService(ProjectTemplateRepository projectTemplateRepository) {
        this.projectTemplateRepository = projectTemplateRepository;
    }

    public List<ProjectTemplateResponse> findAll(ProjectTemplateFilter filter) {
        var spec = ProjectTemplateSpecifications.filterBy(
                filter.areaMin(),
                filter.areaMax(),
                filter.floors()
        );
        return projectTemplateRepository.findAll(spec, Sort.by("id"))
                .stream()
                .map(this::toDtoWithStages)
                .toList();
    }

    public ProjectTemplateResponse findById(Long id) {
        ProjectTemplate template = projectTemplateRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Project template not found"));
        return toDtoWithStages(template);
    }

    private ProjectTemplateResponse toDtoWithStages(ProjectTemplate template) {
        List<ProjectMediaResponse> media = template.getMedia()
                .stream()
                .sorted(Comparator.comparing(ProjectMedia::getSortOrder, Comparator.nullsLast(Integer::compareTo)))
                .map(m -> ProjectMediaResponse.builder()
                        .id(m.getId())
                        .type(m.getType())
                        .url(m.getUrl())
                        .sortOrder(m.getSortOrder())
                        .build())
                .collect(Collectors.toList());
        return ProjectTemplateResponse.builder()
                .id(template.getId())
                .name(template.getName())
                .totalArea(template.getTotalArea())
                .floors(template.getFloors())
                .basePrice(template.getBasePrice())
                .mainMaterials(template.getMainMaterials())
                .description(template.getDescription())
                .defaultStages(template.getDefaultStages())
                .media(media)
                .build();
    }
}
