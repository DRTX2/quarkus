package com.drtx.qks.domain.model;

import java.time.LocalDateTime;

public class ChaosExperiment {
    private String id;
    private String targetNamespace;
    private String targetDeployment;
    private ChaosType type;
    private ChaosStatus status;
    private String executedBy;
    private LocalDateTime executionTime;
    private LocalDateTime completedAt;
    private String executionLogs;
    private String resilienceReport;
    private Integer resilienceScore; // 0 to 100 based on ISO 25010
    private Boolean recoveredSuccessfully;
    private Long recoveryTimeMs;

    public ChaosExperiment(String id, String targetNamespace, String targetDeployment, ChaosType type, String executedBy) {
        this.id = id;
        this.targetNamespace = targetNamespace;
        this.targetDeployment = targetDeployment;
        this.type = type;
        this.status = ChaosStatus.PENDING;
        this.executedBy = executedBy;
        this.executionTime = LocalDateTime.now();
    }

    public ChaosExperiment() {
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTargetNamespace() { return targetNamespace; }
    public void setTargetNamespace(String targetNamespace) { this.targetNamespace = targetNamespace; }

    public String getTargetDeployment() { return targetDeployment; }
    public void setTargetDeployment(String targetDeployment) { this.targetDeployment = targetDeployment; }

    public ChaosType getType() { return type; }
    public void setType(ChaosType type) { this.type = type; }

    public ChaosStatus getStatus() { return status; }
    public void setStatus(ChaosStatus status) { this.status = status; }

    public String getExecutedBy() { return executedBy; }
    public void setExecutedBy(String executedBy) { this.executedBy = executedBy; }

    public LocalDateTime getExecutionTime() { return executionTime; }
    public void setExecutionTime(LocalDateTime executionTime) { this.executionTime = executionTime; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public String getExecutionLogs() { return executionLogs; }
    public void setExecutionLogs(String executionLogs) { this.executionLogs = executionLogs; }

    public String getResilienceReport() { return resilienceReport; }
    public void setResilienceReport(String resilienceReport) { this.resilienceReport = resilienceReport; }

    public Integer getResilienceScore() { return resilienceScore; }
    public void setResilienceScore(Integer resilienceScore) { this.resilienceScore = resilienceScore; }

    public Boolean getRecoveredSuccessfully() { return recoveredSuccessfully; }
    public void setRecoveredSuccessfully(Boolean recoveredSuccessfully) { this.recoveredSuccessfully = recoveredSuccessfully; }

    public Long getRecoveryTimeMs() { return recoveryTimeMs; }
    public void setRecoveryTimeMs(Long recoveryTimeMs) { this.recoveryTimeMs = recoveryTimeMs; }
}
