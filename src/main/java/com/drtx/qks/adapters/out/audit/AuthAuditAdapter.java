package com.drtx.qks.adapters.out.audit;

import com.drtx.qks.adapters.out.persistence.AuthAuditLogEntity;
import com.drtx.qks.domain.model.AuthAuditLog;
import com.drtx.qks.domain.ports.out.audit.AuthAuditPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Adaptador para auditoría de eventos de autenticación
 */
@ApplicationScoped
public class AuthAuditAdapter implements AuthAuditPort {

    private static final Logger log = LoggerFactory.getLogger(AuthAuditAdapter.class);

    @Override
    @Transactional
    public void logAuthEvent(AuthAuditLog auditLog) {
        try {
            AuthAuditLogEntity.logEvent(
                auditLog.getUserId(),
                auditLog.getUsername(),
                auditLog.getEventType().name(),
                auditLog.getIpAddress(),
                auditLog.getUserAgent(),
                auditLog.getDetails()
            );
        } catch (Exception e) {
            log.error("Error al registrar evento de auditoría: {}", auditLog.getEventType(), e);
        }
    }

    @Override
    @Transactional
    public void logLoginSuccess(Long userId, String username, String ipAddress, String userAgent) {
        try {
            AuthAuditLogEntity.logEvent(
                userId,
                username,
                AuthAuditLog.AuthEventType.LOGIN_SUCCESS.name(),
                ipAddress,
                userAgent,
                "Login exitoso"
            );
        } catch (Exception e) {
            log.error("Error al registrar login exitoso para usuario: {}", username, e);
        }
    }

    @Override
    @Transactional
    public void logLoginFailed(String username, String ipAddress, String userAgent, String reason) {
        try {
            AuthAuditLogEntity.logEvent(
                null,
                username,
                AuthAuditLog.AuthEventType.LOGIN_FAILED.name(),
                ipAddress,
                userAgent,
                reason
            );
        } catch (Exception e) {
            log.error("Error al registrar login fallido para usuario: {}", username, e);
        }
    }

    @Override
    @Transactional
    public void logLogout(Long userId, String username, String ipAddress, String userAgent) {
        try {
            AuthAuditLogEntity.logEvent(
                userId,
                username,
                AuthAuditLog.AuthEventType.LOGOUT.name(),
                ipAddress,
                userAgent,
                "Logout exitoso"
            );
        } catch (Exception e) {
            log.error("Error al registrar logout para usuario: {}", username, e);
        }
    }

    @Override
    @Transactional
    public void logPasswordChange(Long userId, String username, String ipAddress, String userAgent) {
        try {
            AuthAuditLogEntity.logEvent(
                userId,
                username,
                AuthAuditLog.AuthEventType.PASSWORD_CHANGE.name(),
                ipAddress,
                userAgent,
                "Password cambiado exitosamente"
            );
        } catch (Exception e) {
            log.error("Error al registrar cambio de password para usuario: {}", username, e);
        }
    }

    @Override
    @Transactional
    public void logTokenRefresh(Long userId, String username, String ipAddress, String userAgent) {
        try {
            AuthAuditLogEntity.logEvent(
                userId,
                username,
                AuthAuditLog.AuthEventType.TOKEN_REFRESH.name(),
                ipAddress,
                userAgent,
                "Token refrescado exitosamente"
            );
        } catch (Exception e) {
            log.error("Error al registrar refresh de token para usuario: {}", username, e);
        }
    }
}

