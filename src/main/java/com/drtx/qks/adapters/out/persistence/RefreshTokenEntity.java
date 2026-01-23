package com.drtx.qks.adapters.out.persistence;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad JPA para Refresh Tokens
 */
@Entity
@Table(name = "refresh_tokens", indexes = {
    @Index(name = "idx_refresh_tokens_token", columnList = "token"),
    @Index(name = "idx_refresh_tokens_user_id", columnList = "user_id"),
    @Index(name = "idx_refresh_tokens_expires_at", columnList = "expires_at"),
    @Index(name = "idx_refresh_tokens_revoked", columnList = "revoked")
})
public class RefreshTokenEntity extends PanacheEntity {

    @Column(name = "token", nullable = false, unique = true, length = 512)
    public String token;

    @Column(name = "user_id", nullable = false)
    public Long userId;

    @Column(name = "expires_at", nullable = false)
    public LocalDateTime expiresAt;

    @Column(name = "revoked", nullable = false)
    public boolean revoked = false;

    @Column(name = "revoked_at")
    public LocalDateTime revokedAt;

    @Column(name = "created_at", nullable = false)
    public LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "ip_address", length = 45)
    public String ipAddress;

    @Column(name = "user_agent", length = 500)
    public String userAgent;

    // Métodos de búsqueda
    public static RefreshTokenEntity findByToken(String token) {
        return find("token", token).firstResult();
    }

    public static long deleteExpiredTokens() {
        return delete("expiresAt < ?1 OR revoked = true", LocalDateTime.now());
    }

    public static long revokeAllUserTokens(Long userId) {
        return update("revoked = true, revokedAt = ?1 WHERE userId = ?2 AND revoked = false",
                     LocalDateTime.now(), userId);
    }
}

