package com.example.constructioncontrol.service;

import com.example.constructioncontrol.dto.ProjectMediaResponse;
import com.example.constructioncontrol.dto.ProjectTemplateCreateRequest;
import com.example.constructioncontrol.dto.ProjectTemplateFilter;
import com.example.constructioncontrol.dto.ProjectTemplateListItemResponse;
import com.example.constructioncontrol.dto.ProjectTemplateResponse;
import com.example.constructioncontrol.dto.ProjectTemplateRangeResponse;
import com.example.constructioncontrol.dto.MaterialOptionResponse;
import com.example.constructioncontrol.model.ProjectMedia;
import com.example.constructioncontrol.model.ProjectTemplate;
import com.example.constructioncontrol.repository.ProjectMediaRepository;
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
    private final ProjectMediaRepository projectMediaRepository;

    public ProjectTemplateService(ProjectTemplateRepository projectTemplateRepository,
                                  ProjectMediaRepository projectMediaRepository) {
        this.projectTemplateRepository = projectTemplateRepository;
        this.projectMediaRepository = projectMediaRepository;
    }

    public List<ProjectTemplateListItemResponse> findAll(ProjectTemplateFilter filter) {
        var spec = ProjectTemplateSpecifications.filterBy(
                filter.areaMin(),
                filter.areaMax(),
                filter.floors(),
                filter.priceMin(),
                filter.priceMax(),
                filter.materials()
        );
        return projectTemplateRepository.findAll(spec, Sort.by("id"))
                .stream()
                .map(this::toListDto)
                .toList();
    }

    public ProjectTemplateResponse findById(Long id) {
        ProjectTemplate template = projectTemplateRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Project template not found"));
        return toDtoWithStages(template);
    }

    public ProjectTemplateRangeResponse getRanges() {
        var r = projectTemplateRepository.findRanges();
        if (r == null) {
            return new ProjectTemplateRangeResponse(null, null, null, null);
        }
        return new ProjectTemplateRangeResponse(
                r.getMinArea(), r.getMaxArea(), r.getMinPrice(), r.getMaxPrice()
        );
    }

    public List<MaterialOptionResponse> getMaterials() {
        return java.util.Arrays.stream(com.example.constructioncontrol.model.MaterialType.values())
                .map(m -> new MaterialOptionResponse(m.name(), m.getDisplayName()))
                .toList();
    }

    @Transactional
    public ProjectTemplateResponse create(ProjectTemplateCreateRequest request) {
        ProjectTemplate template = new ProjectTemplate();
        template.setName(request.getName());
        template.setTotalArea(request.getTotalArea());
        template.setFloors(request.getFloors());
        template.setBasePrice(request.getBasePrice());
        template.setMainMaterials(request.getMainMaterials());
        template.setDescription(request.getDescription());
        if (request.getDefaultStages() != null) {
            template.setDefaultStages(request.getDefaultStages());
        }

        ProjectTemplate saved = projectTemplateRepository.save(template);

        if (request.getMedia() != null) {
            for (var mediaReq : request.getMedia()) {
                ProjectMedia media = new ProjectMedia();
                media.setProjectTemplate(saved);
                media.setType(mediaReq.getType());
                media.setUrl(mediaReq.getUrl());
                media.setSortOrder(mediaReq.getSortOrder());
                projectMediaRepository.save(media);
            }
        }

        ProjectTemplate reloaded = projectTemplateRepository.findById(saved.getId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND));
        return toDtoWithStages(reloaded);
    }

    private ProjectTemplateListItemResponse toListDto(ProjectTemplate template) {
        return ProjectTemplateListItemResponse.builder()
                .id(template.getId())
                .name(template.getName())
                .previewImageUrl(selectPreview(template))
                .basePrice(template.getBasePrice())
                .totalArea(template.getTotalArea())
                .floors(template.getFloors())
                .mainMaterials(template.getMainMaterials() != null ? template.getMainMaterials().getDisplayName() : null)
                .build();
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
                .mainMaterials(template.getMainMaterials() != null ? template.getMainMaterials().getDisplayName() : null)
                .description(template.getDescription())
                .defaultStages(template.getDefaultStages())
                .media(media)
                .build();
    }

    private String selectPreview(ProjectTemplate template) {
        return template.getMedia()
                .stream()
                .sorted(Comparator.comparing(ProjectMedia::getSortOrder, Comparator.nullsLast(Integer::compareTo)))
                .filter(m -> m.getType() != null)
                .sorted(Comparator.comparing((ProjectMedia m) -> m.getType() == null ? 1 : 0)
                        .thenComparing(ProjectMedia::getType))
                .findFirst()
                .map(ProjectMedia::getUrl)
                .orElse(null);
    }
}
