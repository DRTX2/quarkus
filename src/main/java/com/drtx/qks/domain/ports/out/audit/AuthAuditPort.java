package com.drtx.qks.domain.ports.out.audit;

import com.drtx.qks.domain.model.AuthAuditLog;

public interface AuthAuditPort {

    void logAuthEvent(AuthAuditLog auditLog);

    void logLoginSuccess(Long userId, String username, String ipAddress, String userAgent);

    void logLoginFailed(String username, String ipAddress, String userAgent, String reason);

    void logLogout(Long userId, String username, String ipAddress, String userAgent);

    void logPasswordChange(Long userId, String username, String ipAddress, String userAgent);

    void logTokenRefresh(Long userId, String username, String ipAddress, String userAgent);
}

