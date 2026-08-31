package com.drtx.qks.domain.model;

public class ChaosExecutionResult {
    private boolean success;
    private String logs;
    private Long recoveryTimeMs;

    public ChaosExecutionResult(boolean success, String logs, Long recoveryTimeMs) {
        this.success = success;
        this.logs = logs;
        this.recoveryTimeMs = recoveryTimeMs;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getLogs() { return logs; }
    public void setLogs(String logs) { this.logs = logs; }

    public Long getRecoveryTimeMs() { return recoveryTimeMs; }
    public void setRecoveryTimeMs(Long recoveryTimeMs) { this.recoveryTimeMs = recoveryTimeMs; }
}
