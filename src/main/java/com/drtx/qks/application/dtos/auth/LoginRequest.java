package com.drtx.qks.application.dtos.auth;
import jakarta.validation.constraints.NotBlank;
public record LoginRequest(
        @NotBlank(message = "El username o email es requerido")
        String usernameOrEmail,
        @NotBlank(message = "El password es requerido")
        String password
) {
}
