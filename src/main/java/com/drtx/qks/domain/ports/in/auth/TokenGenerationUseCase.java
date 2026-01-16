package com.drtx.qks.domain.ports.in.auth;

/**
 * Puerto de entrada para generación de tokens JWT
 */
public interface TokenGenerationUseCase {

    /**
     * Genera un token JWT para un usuario
     */
    String generateToken(Long userId, String username, String email, java.util.Set<String> roles);

    /**
     * Obtiene la duración del token en segundos
     */
    Long getTokenDuration();
}

