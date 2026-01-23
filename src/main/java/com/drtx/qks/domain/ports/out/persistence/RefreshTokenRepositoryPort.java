package com.drtx.qks.domain.ports.out.persistence;

import com.drtx.qks.domain.model.RefreshToken;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepositoryPort {

    RefreshToken save(RefreshToken refreshToken);

    Optional<RefreshToken> findByToken(String token);

    void revokeAllUserTokens(UUID userId);

    void deleteExpiredTokens();

    long countActiveTokensByUser(UUID userId);
}

