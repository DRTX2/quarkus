package com.drtx.qks.application.dtos.auth;

/**
 * Respuesta de autenticación exitosa con access y refresh tokens
 */
public record AuthResponse(
        String accessToken,
        String refreshToken,
        String type,
        Long expiresIn,
        UserInfo user
) {
    public AuthResponse(String accessToken, String refreshToken, Long expiresIn, UserInfo user) {
        this(accessToken, refreshToken, "Bearer", expiresIn, user);
    }
}
