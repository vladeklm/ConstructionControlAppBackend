package com.example.constructioncontrol.dto;

public class StageCompleteResponse {

    private StageResponse completedStage;
    private StageResponse nextStage;

    public StageResponse getCompletedStage() {
        return completedStage;
    }

    public void setCompletedStage(StageResponse completedStage) {
        this.completedStage = completedStage;
    }

    public StageResponse getNextStage() {
        return nextStage;
    }

    public void setNextStage(StageResponse nextStage) {
        this.nextStage = nextStage;
    }
}
