package com.drtx.qks.application.dtos.auth;

import jakarta.validation.constraints.NotBlank;

/**
 * Request para refrescar el access token usando un refresh token
 */
public record RefreshTokenRequest(
    @NotBlank(message = "El refresh token es requerido")
    String refreshToken
) {}

