package com.drtx.qks.application.dtos.auth;
import jakarta.validation.constraints.Size;
public record ChangePasswordRequest(
        @Size(min = 6, message = "El password debe tener al menos 6 caracteres")
        String currentPassword,
        @Size(min = 6, message = "El nuevo password debe tener al menos 6 caracteres")
        String newPassword
) {
}
