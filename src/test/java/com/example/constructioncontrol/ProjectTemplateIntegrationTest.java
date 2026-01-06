package com.example.constructioncontrol;

import com.example.constructioncontrol.model.ProjectMedia;
import com.example.constructioncontrol.model.ProjectMediaType;
import com.example.constructioncontrol.model.ProjectTemplate;
import com.example.constructioncontrol.model.MaterialType;
import com.example.constructioncontrol.model.StageType;
import com.example.constructioncontrol.repository.ProjectMediaRepository;
import com.example.constructioncontrol.repository.ProjectTemplateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProjectTemplateIntegrationTest {

    @Autowired
    private ProjectTemplateRepository projectTemplateRepository;

    @Autowired
    private ProjectMediaRepository projectMediaRepository;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setupData() {
        projectMediaRepository.deleteAll();
        projectTemplateRepository.deleteAll();

        ProjectTemplate template = new ProjectTemplate();
        template.setName("Минимод 90");
        template.setTotalArea(new BigDecimal("90.0"));
        template.setFloors(1);
        template.setBasePrice(new BigDecimal("8900000.00"));
        template.setMainMaterials(MaterialType.FRAME);
        template.setDescription("Одноэтажный лаконичный дом для небольшого участка.");
        template.setDefaultStages(List.of(
                StageType.PREPARATION,
                StageType.FOUNDATION,
                StageType.WALLS,
                StageType.ROOFING
        ));

        ProjectTemplate saved = projectTemplateRepository.save(template);

        ProjectMedia media = new ProjectMedia();
        media.setProjectTemplate(saved);
        media.setType(ProjectMediaType.RENDER);
        media.setUrl("https://cdn.example.com/projects/minimod90/render-1.jpg");
        media.setSortOrder(1);
        projectMediaRepository.save(media);
    }

    @Test
    void repositoryShouldReturnStoredTemplate() {
        List<ProjectTemplate> all = projectTemplateRepository.findAll();
        assertThat(all).hasSize(1);
        assertThat(all.get(0).getName()).isEqualTo("Минимод 90");
    }


    @Test
    void controllerShouldReturnSingleTemplate() throws Exception {
        Long id = projectTemplateRepository.findAll().get(0).getId();
        mockMvc.perform(get("/api/projects/{id}", id).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Минимод 90"))
                .andExpect(jsonPath("$.defaultStages[0]").value("PREPARATION"));
    }
}
