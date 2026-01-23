package com.drtx.qks.application.services.auth;

import com.drtx.qks.domain.exceptions.AuthenticationException;
import com.drtx.qks.domain.model.RefreshToken;
import com.drtx.qks.domain.ports.in.auth.RefreshTokenUseCase;
import com.drtx.qks.domain.ports.out.persistence.RefreshTokenRepositoryPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

/**
 * Servicio para gestión de refresh tokens
 */
@ApplicationScoped
public class RefreshTokenService implements RefreshTokenUseCase {

    private static final int TOKEN_LENGTH = 64;
    private static final SecureRandom secureRandom = new SecureRandom();

    @ConfigProperty(name = "jwt.refresh-token.duration", defaultValue = "604800") // 7 días
    Long refreshTokenDuration;

    @ConfigProperty(name = "jwt.refresh-token.max-per-user", defaultValue = "5")
    int maxTokensPerUser;

    private final RefreshTokenRepositoryPort refreshTokenRepository;

    @Inject
    public RefreshTokenService(RefreshTokenRepositoryPort refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    public RefreshToken generateRefreshToken(UUID userId, String ipAddress, String userAgent) {
        // Limpiar tokens antiguos si el usuario tiene demasiados
        long activeTokens = refreshTokenRepository.countActiveTokensByUser(userId);
        if (activeTokens >= maxTokensPerUser) {
            refreshTokenRepository.revokeAllUserTokens(userId);
        }

        String token = generateSecureToken();
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(refreshTokenDuration);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        refreshToken.setUserId(userId);
        refreshToken.setExpiresAt(expiresAt);
        refreshToken.setRevoked(false);
        refreshToken.setCreatedAt(LocalDateTime.now());
        refreshToken.setIpAddress(ipAddress);
        refreshToken.setUserAgent(userAgent);

        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public RefreshToken validateRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
            .orElseThrow(() -> new AuthenticationException("Refresh token inválido"));

        if (refreshToken.isRevoked()) {
            throw new AuthenticationException("Refresh token revocado");
        }

        if (refreshToken.isExpired()) {
            throw new AuthenticationException("Refresh token expirado");
        }

        return refreshToken;
    }

    @Override
    public void revokeRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
            .orElseThrow(() -> new AuthenticationException("Refresh token no encontrado"));

        refreshToken.revoke();
        refreshTokenRepository.save(refreshToken);
    }

    @Override
    public void revokeAllUserTokens(UUID userId) {
        refreshTokenRepository.revokeAllUserTokens(userId);
    }

    @Override
    public void cleanupExpiredTokens() {
        refreshTokenRepository.deleteExpiredTokens();
    }

    /**
     * Genera un token seguro aleatorio
     */
    private String generateSecureToken() {
        byte[] randomBytes = new byte[TOKEN_LENGTH];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }
}

