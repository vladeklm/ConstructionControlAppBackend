package com.example.constructioncontrol.dto;

import com.example.constructioncontrol.model.StageReportStatus;
import lombok.Data;
import java.time.LocalDate;

@Data
public class CreateStageReportRequest {
    private String title;
    private LocalDate reportDate;
    private StageReportStatus status;
    private String comment;
    private String pdfUrl;
}