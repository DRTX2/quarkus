package com.drtx.qks.domain.model;

import java.time.LocalDateTime;

/**
 * Registro de auditoría para eventos de autenticación
 */
public class AuthAuditLog {
    private Long id;
    private Long userId;
    private String username;
    private AuthEventType eventType;
    private String ipAddress;
    private String userAgent;
    private String details;
    private LocalDateTime createdAt;

    public enum AuthEventType {
        LOGIN_SUCCESS,
        LOGIN_FAILED,
        LOGOUT,
        PASSWORD_CHANGE,
        PASSWORD_RESET_REQUEST,
        PASSWORD_RESET_COMPLETE,
        TOKEN_REFRESH,
        TOKEN_REVOKE,
        ACCOUNT_LOCKED,
        ACCOUNT_UNLOCKED
    }

    public AuthAuditLog() {
    }

    public AuthAuditLog(Long userId, String username, AuthEventType eventType,
                       String ipAddress, String userAgent, String details) {
        this.userId = userId;
        this.username = username;
        this.eventType = eventType;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.details = details;
        this.createdAt = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public AuthEventType getEventType() {
        return eventType;
    }

    public void setEventType(AuthEventType eventType) {
        this.eventType = eventType;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

