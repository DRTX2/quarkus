package com.drtx.qks.application.dtos.auth;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
public record RegisterRequest(
        @NotBlank(message = "El username es requerido")
        @Size(min = 3, max = 50, message = "El username debe tener entre 3 y 50 caracteres")
        String username,
        @NotBlank(message = "El email es requerido")
        @Email(message = "El email debe ser valido")
        String email,
        @NotBlank(message = "El password es requerido")
        @Size(min = 6, message = "El password debe tener al menos 6 caracteres")
        String password
) {
}
