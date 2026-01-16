package com.drtx.qks.adapters.in.rest.auth;

import com.drtx.qks.application.dtos.auth.*;
import com.drtx.qks.application.mappers.UserInfoMapper;
import com.drtx.qks.domain.exceptions.AuthenticationException;
import com.drtx.qks.domain.exceptions.BusinessException;
import com.drtx.qks.domain.model.User;
import com.drtx.qks.domain.ports.in.auth.AuthenticationUseCase;
import com.drtx.qks.domain.ports.in.auth.TokenGenerationUseCase;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * Controlador REST para autenticación
 */
@Path("/api/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Authentication", description = "Endpoints de autenticación y registro")
public class AuthController {

    @Inject
    AuthenticationUseCase authenticationService;

    @Inject
    TokenGenerationUseCase tokenService;

    @Inject
    UserInfoMapper userInfoMapper;

    @Inject
    JsonWebToken jwt;

    /**
     * Registro de nuevo usuario
     */
    @POST
    @Path("/register")
    @PermitAll
    @Operation(summary = "Registrar nuevo usuario", description = "Crea un nuevo usuario en el sistema")
    @APIResponse(responseCode = "201", description = "Usuario creado exitosamente",
            content = @Content(schema = @Schema(implementation = AuthResponse.class)))
    @APIResponse(responseCode = "400", description = "Datos inválidos")
    @APIResponse(responseCode = "409", description = "Usuario o email ya existe")
    public Response register(@Valid RegisterRequest request) {
        try {
            // Registrar usuario
            User user = authenticationService.register(
                    request.username(),
                    request.email(),
                    request.password()
            );

            // Generar token
            String token = tokenService.generateToken(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getRoles()
            );

            // Construir respuesta
            UserInfo userInfo = userInfoMapper.toUserInfo(user);
            AuthResponse response = new AuthResponse(token, tokenService.getTokenDuration(), userInfo);

            return Response.status(Response.Status.CREATED).entity(response).build();

        } catch (BusinessException e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(new ErrorMessage(e.getMessage()))
                    .build();
        }
    }

    /**
     * Login de usuario
     */
    @POST
    @Path("/login")
    @PermitAll
    @Operation(summary = "Login de usuario", description = "Autentica un usuario y devuelve un token JWT")
    @APIResponse(responseCode = "200", description = "Login exitoso",
            content = @Content(schema = @Schema(implementation = AuthResponse.class)))
    @APIResponse(responseCode = "401", description = "Credenciales inválidas")
    public Response login(@Valid LoginRequest request) {
        try {
            // Autenticar usuario
            User user = authenticationService.authenticate(
                    request.usernameOrEmail(),
                    request.password()
            );

            // Generar token
            String token = tokenService.generateToken(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getRoles()
            );

            // Construir respuesta
            UserInfo userInfo = userInfoMapper.toUserInfo(user);
            AuthResponse response = new AuthResponse(token, tokenService.getTokenDuration(), userInfo);

            return Response.ok(response).build();

        } catch (AuthenticationException e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new ErrorMessage(e.getMessage()))
                    .build();
        }
    }

    /**
     * Obtener información del usuario actual
     */
    @GET
    @Path("/me")
    @RolesAllowed({"USER", "ADMIN"})
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(summary = "Información del usuario actual",
            description = "Retorna información del usuario autenticado desde el token JWT")
    @APIResponse(responseCode = "200", description = "Usuario encontrado",
            content = @Content(schema = @Schema(implementation = UserInfo.class)))
    @APIResponse(responseCode = "401", description = "No autenticado")
    public Response getCurrentUser() {
        if (jwt == null || jwt.getName() == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new ErrorMessage("Token no válido"))
                    .build();
        }

        var userInfo = new UserInfo(
                Long.parseLong(jwt.getClaim("userId")),
                jwt.getName(),
                jwt.getClaim("email"),
                jwt.getGroups(),
                true // enabled (del token no podemos saberlo, asumimos true)
        );

        return Response.ok(userInfo).build();
    }

    /**
     * Cambiar password del usuario actual
     */
    @PUT
    @Path("/change-password")
    @RolesAllowed({"USER", "ADMIN"})
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(summary = "Cambiar password", description = "Cambia el password del usuario autenticado")
    @APIResponse(responseCode = "200", description = "Password cambiado exitosamente")
    @APIResponse(responseCode = "400", description = "Password actual incorrecto")
    @APIResponse(responseCode = "401", description = "No autenticado")
    public Response changePassword(@Valid ChangePasswordRequest request) {
        try {
            Long userId = Long.parseLong(jwt.getClaim("userId"));

            authenticationService.changePassword(
                    userId,
                    request.currentPassword(),
                    request.newPassword()
            );

            return Response.ok(new SuccessMessage("Password actualizado correctamente")).build();

        } catch (AuthenticationException | BusinessException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorMessage(e.getMessage()))
                    .build();
        }
    }

    /**
     * Endpoint de prueba para ADMIN
     */
    @GET
    @Path("/admin")
    @RolesAllowed("ADMIN")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(summary = "Endpoint de administrador",
            description = "Solo accesible para usuarios con rol ADMIN")
    @APIResponse(responseCode = "200", description = "Acceso concedido")
    @APIResponse(responseCode = "403", description = "Acceso denegado")
    public Response adminEndpoint() {
        return Response.ok(new SuccessMessage("Acceso a endpoint de administrador concedido")).build();
    }

    // DTOs internos
    public record ErrorMessage(String error) {}
    public record SuccessMessage(String message) {}
}

