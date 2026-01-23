package com.drtx.qks.infrastructure.scheduler;

import com.drtx.qks.domain.ports.in.auth.RefreshTokenUseCase;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Scheduler para tareas de mantenimiento de seguridad
 */
@ApplicationScoped
public class SecurityMaintenanceScheduler {

    private static final Logger log = LoggerFactory.getLogger(SecurityMaintenanceScheduler.class);

    private final RefreshTokenUseCase refreshTokenService;

    @Inject
    public SecurityMaintenanceScheduler(RefreshTokenUseCase refreshTokenService) {
        this.refreshTokenService = refreshTokenService;
    }

    /**
     * Limpia tokens expirados cada hora
     */
    @Scheduled(cron = "0 0 * * * ?") // Cada hora en punto
    void cleanupExpiredTokens() {
        log.info("Iniciando limpieza de refresh tokens expirados...");
        try {
            refreshTokenService.cleanupExpiredTokens();
            log.info("Limpieza de tokens completada exitosamente");
        } catch (Exception e) {
            log.error("Error al limpiar tokens expirados", e);
        }
    }
}

