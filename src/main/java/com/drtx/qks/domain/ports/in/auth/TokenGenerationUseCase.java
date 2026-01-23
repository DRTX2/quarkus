package com.drtx.qks.domain.ports.in.auth;

import java.util.Set;
import java.util.UUID;

public interface TokenGenerationUseCase {
    String generateToken(UUID userId, String username, String email, Set<String> roles);
    Long getTokenDuration();
}
