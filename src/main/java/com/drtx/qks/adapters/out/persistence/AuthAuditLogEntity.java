package com.drtx.qks.adapters.out.persistence;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad JPA para auditoría de autenticación
 */
@Entity
@Table(name = "auth_audit_log", indexes = {
    @Index(name = "idx_auth_audit_user_id", columnList = "user_id"),
    @Index(name = "idx_auth_audit_event_type", columnList = "event_type"),
    @Index(name = "idx_auth_audit_created_at", columnList = "created_at")
})
public class AuthAuditLogEntity extends PanacheEntity {

    @Column(name = "user_id")
    public Long userId;

    @Column(name = "username", length = 50)
    public String username;

    @Column(name = "event_type", nullable = false, length = 50)
    public String eventType;

    @Column(name = "ip_address", length = 45)
    public String ipAddress;

    @Column(name = "user_agent", length = 500)
    public String userAgent;

    @Column(name = "details", columnDefinition = "TEXT")
    public String details;

    @Column(name = "created_at", nullable = false)
    public LocalDateTime createdAt = LocalDateTime.now();

    // Métodos de búsqueda
    public static void logEvent(Long userId, String username, String eventType,
                               String ipAddress, String userAgent, String details) {
        AuthAuditLogEntity log = new AuthAuditLogEntity();
        log.userId = userId;
        log.username = username;
        log.eventType = eventType;
        log.ipAddress = ipAddress;
        log.userAgent = userAgent;
        log.details = details;
        log.createdAt = LocalDateTime.now();
        log.persist();
    }
}

