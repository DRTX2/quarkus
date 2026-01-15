package com.drtx.qks.security;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.Set;

/**
 * Resource para autenticación JWT
 */
@Path("/api/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Authentication", description = "Endpoints de autenticación JWT")
public class AuthResource {

    @Inject
    JwtService jwtService;

    @Inject
    JsonWebToken jwt;

    @ConfigProperty(name = "JWT_DURATION", defaultValue = "3600")
    Long tokenDuration;

    /**
     * Login - Genera un token JWT
     *
     * NOTA: Este es un ejemplo básico. En producción deberías:
     * - Validar credenciales contra base de datos
     * - Hashear passwords con BCrypt
     * - Implementar rate limiting
     * - Agregar logs de auditoría
     */
    @POST
    @Path("/login")
    @PermitAll
    @Operation(summary = "Login de usuario", description = "Genera un token JWT válido")
    public Response login(LoginRequest request) {
        // Validación básica
        if (request.username == null || request.password == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorMessage("Username y password son requeridos"))
                    .build();
        }

        // TODO: Validar credenciales contra base de datos
        // Por ahora, ejemplo simplificado:
        if ("admin".equals(request.username) && "admin".equals(request.password)) {
            String token = jwtService.generateTokenWithClaims(
                    request.username,
                    Set.of("admin", "user"),
                    "admin@example.com",
                    "1"
            );

            return Response.ok(new LoginResponse(token, tokenDuration)).build();
        } else if ("user".equals(request.username) && "user".equals(request.password)) {
            String token = jwtService.generateTokenWithClaims(
                    request.username,
                    Set.of("user"),
                    "user@example.com",
                    "2"
            );

            return Response.ok(new LoginResponse(token, tokenDuration)).build();
        }

        return Response.status(Response.Status.UNAUTHORIZED)
                .entity(new ErrorMessage("Credenciales inválidas"))
                .build();
    }

    /**
     * Obtener información del usuario actual desde el token
     */
    @GET
    @Path("/me")
    @RolesAllowed({"user", "admin"})
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(summary = "Información del usuario actual",
               description = "Retorna información del usuario autenticado desde el token JWT")
    public Response getCurrentUser() {
        if (jwt == null || jwt.getName() == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new ErrorMessage("Token no válido"))
                    .build();
        }

        var userInfo = new UserInfo(
                jwt.getName(),
                jwt.getGroups(),
                jwt.getClaim("email"),
                jwt.getClaim("userId")
        );

        return Response.ok(userInfo).build();
    }

    /**
     * Endpoint protegido solo para administradores
     */
    @GET
    @Path("/admin")
    @RolesAllowed("admin")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(summary = "Endpoint de administrador",
               description = "Solo accesible para usuarios con rol 'admin'")
    public Response adminEndpoint() {
        return Response.ok(new SuccessMessage("Acceso a endpoint de administrador concedido")).build();
    }

    // DTOs internos
    public static class ErrorMessage {
        public String error;

        public ErrorMessage(String error) {
            this.error = error;
        }
    }

    public static class SuccessMessage {
        public String message;

        public SuccessMessage(String message) {
            this.message = message;
        }
    }

    public static class UserInfo {
        public String username;
        public Set<String> roles;
        public String email;
        public String userId;

        public UserInfo(String username, Set<String> roles, String email, String userId) {
            this.username = username;
            this.roles = roles;
            this.email = email;
            this.userId = userId;
        }
    }
}

