package com.drtx.qks.domain.model;

import java.util.List;

public class ResilienceEvaluation {
    private Integer score; // 0-100 ISO 25010
    private Boolean recoveredSuccessfully;
    private Long recoveryTimeMs;
    private String riskLevel; // LOW, MEDIUM, HIGH, CRITICAL
    private String reportMarkdown;
    private List<String> recommendations;

    public ResilienceEvaluation() {
    }

    public ResilienceEvaluation(Integer score, Boolean recoveredSuccessfully, Long recoveryTimeMs, String riskLevel, String reportMarkdown, List<String> recommendations) {
        this.score = score;
        this.recoveredSuccessfully = recoveredSuccessfully;
        this.recoveryTimeMs = recoveryTimeMs;
        this.riskLevel = riskLevel;
        this.reportMarkdown = reportMarkdown;
        this.recommendations = recommendations;
    }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    public Boolean getRecoveredSuccessfully() { return recoveredSuccessfully; }
    public void setRecoveredSuccessfully(Boolean recoveredSuccessfully) { this.recoveredSuccessfully = recoveredSuccessfully; }

    public Long getRecoveryTimeMs() { return recoveryTimeMs; }
    public void setRecoveryTimeMs(Long recoveryTimeMs) { this.recoveryTimeMs = recoveryTimeMs; }

    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }

    public String getReportMarkdown() { return reportMarkdown; }
    public void setReportMarkdown(String reportMarkdown) { this.reportMarkdown = reportMarkdown; }

    public List<String> getRecommendations() { return recommendations; }
    public void setRecommendations(List<String> recommendations) { this.recommendations = recommendations; }
}
