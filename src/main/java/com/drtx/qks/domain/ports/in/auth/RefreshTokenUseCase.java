package com.drtx.qks.domain.ports.in.auth;

import com.drtx.qks.domain.model.RefreshToken;
import java.util.UUID;

public interface RefreshTokenUseCase {
    RefreshToken generateRefreshToken(UUID userId, String ipAddress, String userAgent);
    RefreshToken validateRefreshToken(String token);
    void revokeRefreshToken(String token);
    void revokeAllUserTokens(UUID userId);
    void cleanupExpiredTokens();
}

