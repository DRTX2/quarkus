package com.drtx.qks.adapters.out.persistence;

import com.drtx.qks.domain.model.RefreshToken;
import com.drtx.qks.domain.ports.out.persistence.RefreshTokenRepositoryPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class RefreshTokenRepositoryAdapter implements RefreshTokenRepositoryPort {

    @Override
    @Transactional
    public RefreshToken save(RefreshToken refreshToken) {
        RefreshTokenEntity entity = new RefreshTokenEntity();
        entity.token = refreshToken.getToken();
        entity.userId = getUserIdByUuid(refreshToken.getUserId());
        entity.expiresAt = refreshToken.getExpiresAt();
        entity.revoked = refreshToken.isRevoked();
        entity.revokedAt = refreshToken.getRevokedAt();
        entity.createdAt = refreshToken.getCreatedAt();
        entity.ipAddress = refreshToken.getIpAddress();
        entity.userAgent = refreshToken.getUserAgent();

        entity.persist();

        refreshToken.setId(entity.id);
        return refreshToken;
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        RefreshTokenEntity entity = RefreshTokenEntity.findByToken(token);
        if (entity == null) {
            return Optional.empty();
        }

        UserEntity userEntity = UserEntity.findById(entity.userId);
        if (userEntity == null) {
            return Optional.empty();
        }

        RefreshToken refreshToken = new RefreshToken(
            entity.id,
            entity.token,
            userEntity.getUuid(),
            entity.expiresAt,
            entity.revoked,
            entity.revokedAt,
            entity.createdAt,
            entity.ipAddress,
            entity.userAgent
        );

        return Optional.of(refreshToken);
    }

    @Override
    @Transactional
    public void revokeAllUserTokens(UUID userId) {
        Long userIdLong = getUserIdByUuid(userId);
        RefreshTokenEntity.revokeAllUserTokens(userIdLong);
    }

    @Override
    @Transactional
    public void deleteExpiredTokens() {
        RefreshTokenEntity.deleteExpiredTokens();
    }

    @Override
    public long countActiveTokensByUser(UUID userId) {
        Long userIdLong = getUserIdByUuid(userId);
        return RefreshTokenEntity.count("userId = ?1 AND revoked = false AND expiresAt > NOW()", userIdLong);
    }

    private Long getUserIdByUuid(UUID uuid) {
        UserEntity user = UserEntity.find("uuid", uuid).firstResult();
        if (user == null) {
            throw new RuntimeException("Usuario no encontrado con UUID: " + uuid);
        }
        return user.getId();
    }
}

