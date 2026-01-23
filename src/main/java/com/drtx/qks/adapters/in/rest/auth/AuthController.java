package com.drtx.qks.adapters.in.rest.auth;

import com.drtx.qks.application.dtos.auth.*;
import com.drtx.qks.application.mappers.UserInfoMapper;
import com.drtx.qks.domain.constants.JwtConstants;
import com.drtx.qks.domain.constants.Roles;
import com.drtx.qks.domain.model.RefreshToken;
import com.drtx.qks.domain.model.User;
import com.drtx.qks.domain.ports.in.auth.AuthenticationUseCase;
import com.drtx.qks.domain.ports.in.auth.RefreshTokenUseCase;
import com.drtx.qks.domain.ports.in.auth.TokenGenerationUseCase;
import com.drtx.qks.domain.ports.out.audit.AuthAuditPort;
import com.drtx.qks.domain.ports.out.persistence.UserRepositoryPort;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.UUID;

@Path("/api/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Authentication", description = "Endpoints de autenticación y gestión de tokens")
public class AuthController {

        private final AuthenticationUseCase authenticationService;
        private final TokenGenerationUseCase tokenService;
        private final RefreshTokenUseCase refreshTokenService;
        private final UserRepositoryPort userRepository;
        private final AuthAuditPort authAuditPort;
        private final UserInfoMapper userInfoMapper;
        private final JsonWebToken jwt;

        @Inject
        public AuthController(
                        AuthenticationUseCase authenticationService,
                        TokenGenerationUseCase tokenService,
                        RefreshTokenUseCase refreshTokenService,
                        UserRepositoryPort userRepository,
                        AuthAuditPort authAuditPort,
                        UserInfoMapper userInfoMapper,
                        JsonWebToken jwt) {
                this.authenticationService = authenticationService;
                this.tokenService = tokenService;
                this.refreshTokenService = refreshTokenService;
                this.userRepository = userRepository;
                this.authAuditPort = authAuditPort;
                this.userInfoMapper = userInfoMapper;
                this.jwt = jwt;
        }

        @POST
        @Path("/register")
        @PermitAll
        @Operation(summary = "Registrar nuevo usuario", description = "Crea un nuevo usuario en el sistema")
        @APIResponse(responseCode = "201", description = "Usuario creado exitosamente",
                     content = @Content(schema = @Schema(implementation = AuthResponse.class)))
        @APIResponse(responseCode = "400", description = "Datos inválidos")
        @APIResponse(responseCode = "409", description = "Usuario o email ya existe")
        public Response register(@Valid RegisterRequest request, @Context UriInfo uriInfo) {
                User user = authenticationService.register(
                                request.username(),
                                request.email(),
                                request.password());

                String accessToken = tokenService.generateToken(
                                user.getUuid(),
                                user.getUsername(),
                                user.getEmail(),
                                user.getRoles());

                String clientIp = getClientIp(uriInfo);
                String userAgent = getUserAgent();
                RefreshToken refreshToken = refreshTokenService.generateRefreshToken(
                                user.getUuid(),
                                clientIp,
                                userAgent);

                UserInfo userInfo = userInfoMapper.toUserInfo(user);
                AuthResponse response = new AuthResponse(
                                accessToken,
                                refreshToken.getToken(),
                                tokenService.getTokenDuration(),
                                userInfo);

                return Response.status(Response.Status.CREATED).entity(response).build();
        }

        @POST
        @Path("/login")
        @PermitAll
        @Operation(summary = "Login de usuario", description = "Autentica un usuario y devuelve tokens JWT")
        @APIResponse(
                responseCode = "200",
                description = "Login exitoso",
                content = @Content(schema = @Schema(implementation = AuthResponse.class)))
        @APIResponse(responseCode = "401", description = "Credenciales inválidas")
        public Response login(@Valid LoginRequest request, @Context UriInfo uriInfo) {
                User user = authenticationService.authenticate(
                                request.usernameOrEmail(),
                                request.password());

                String accessToken = tokenService.generateToken(
                                user.getUuid(),
                                user.getUsername(),
                                user.getEmail(),
                                user.getRoles());

                String clientIp = getClientIp(uriInfo);
                String userAgent = getUserAgent();
                RefreshToken refreshToken = refreshTokenService.generateRefreshToken(
                                user.getUuid(),
                                clientIp,
                                userAgent);

                UserInfo userInfo = userInfoMapper.toUserInfo(user);
                AuthResponse response = new AuthResponse(
                                accessToken,
                                refreshToken.getToken(),
                                tokenService.getTokenDuration(),
                                userInfo);

                return Response.ok(response).build();
        }

        @POST
        @Path("/refresh")
        @PermitAll
        @Operation(
                summary = "Refrescar access token",
                description = "Genera un nuevo access token usando un refresh token válido"
        )
        @APIResponse(
                responseCode = "200",
                description = "Token refrescado exitosamente",
                content = @Content(schema = @Schema(implementation = AuthResponse.class))
        )
        @APIResponse(responseCode = "401", description = "Refresh token inválido o expirado")
        public Response refreshToken(@Valid RefreshTokenRequest request, @Context UriInfo uriInfo) {
                RefreshToken refreshToken = refreshTokenService.validateRefreshToken(request.refreshToken());

                User user = userRepository.findByUuid(refreshToken.getUserId())
                                .orElseThrow(() -> new WebApplicationException("Usuario no encontrado",
                                            Response.Status.UNAUTHORIZED));

                String newAccessToken = tokenService.generateToken(
                                user.getUuid(),
                                user.getUsername(),
                                user.getEmail(),
                                user.getRoles());

                String clientIp = getClientIp(uriInfo);
                String userAgent = getUserAgent();
                RefreshToken newRefreshToken = refreshTokenService.generateRefreshToken(
                                user.getUuid(),
                                clientIp,
                                userAgent);

                // Revocar el refresh token antiguo
                refreshTokenService.revokeRefreshToken(request.refreshToken());

                authAuditPort.logTokenRefresh(user.getId(), user.getUsername(), clientIp, userAgent);

                UserInfo userInfo = userInfoMapper.toUserInfo(user);
                AuthResponse response = new AuthResponse(
                                newAccessToken,
                                newRefreshToken.getToken(),
                                tokenService.getTokenDuration(),
                                userInfo);

                return Response.ok(response).build();
        }

        @POST
        @Path("/logout")
        @RolesAllowed({ Roles.USER, Roles.ADMIN })
        @SecurityRequirement(name = "bearer-jwt")
        @Operation(summary = "Logout", description = "Revoca todos los refresh tokens del usuario")
        @APIResponse(responseCode = "200", description = "Logout exitoso")
        public Response logout(@Context UriInfo uriInfo) {
                UUID userId = UUID.fromString(jwt.getClaim(JwtConstants.CLAIM_USER_ID));
                String username = jwt.getName();

                refreshTokenService.revokeAllUserTokens(userId);

                String clientIp = getClientIp(uriInfo);
                String userAgent = getUserAgent();
                User user = userRepository.findByUuid(userId).orElse(null);
                if (user != null) {
                        authAuditPort.logLogout(user.getId(), username, clientIp, userAgent);
                }

                return Response.ok(new SuccessMessage("Logout exitoso")).build();
        }

        @GET
        @Path("/me")
        @RolesAllowed({ Roles.USER, Roles.ADMIN })
        @SecurityRequirement(name = "bearer-jwt")
        @Operation(summary = "Información del usuario actual",
                   description = "Retorna información del usuario autenticado")
        @APIResponse(responseCode = "200", description = "Usuario encontrado",
                     content = @Content(schema = @Schema(implementation = UserInfo.class)))
        @APIResponse(responseCode = "401", description = "No autenticado")
        public Response getCurrentUser() {
                if (jwt == null || jwt.getName() == null) {
                        return Response.status(Response.Status.UNAUTHORIZED)
                                        .entity(new ErrorMessage("Token no válido"))
                                        .build();
                }

                UUID userId = UUID.fromString(jwt.getClaim(JwtConstants.CLAIM_USER_ID));
                User user = userRepository.findByUuid(userId)
                                .orElseThrow(() -> new WebApplicationException("Usuario no encontrado",
                                            Response.Status.NOT_FOUND));

                UserInfo userInfo = userInfoMapper.toUserInfo(user);
                return Response.ok(userInfo).build();
        }

        @PUT
        @Path("/change-password")
        @RolesAllowed({ Roles.USER, Roles.ADMIN })
        @SecurityRequirement(name = "bearer-jwt")
        @Operation(summary = "Cambiar password", description = "Cambia el password del usuario autenticado")
        @APIResponse(responseCode = "200", description = "Password cambiado exitosamente")
        @APIResponse(responseCode = "400", description = "Password actual incorrecto")
        @APIResponse(responseCode = "401", description = "No autenticado")
        public Response changePassword(@Valid ChangePasswordRequest request) {
                UUID userId = UUID.fromString(jwt.getClaim(JwtConstants.CLAIM_USER_ID));

                authenticationService.changePassword(
                                userId,
                                request.currentPassword(),
                                request.newPassword());

                // Revocar todos los tokens del usuario por seguridad
                refreshTokenService.revokeAllUserTokens(userId);

                return Response.ok(new SuccessMessage("Password actualizado correctamente. " +
                                "Por favor, inicie sesión nuevamente.")).build();
        }

        @GET
        @Path("/admin")
        @RolesAllowed(Roles.ADMIN)
        @SecurityRequirement(name = "bearer-jwt")
        @Operation(summary = "Endpoint de administrador", description = "Solo accesible para ADMIN")
        @APIResponse(responseCode = "200", description = "Acceso concedido")
        @APIResponse(responseCode = "403", description = "Acceso denegado")
        public Response adminEndpoint() {
                return Response.ok(new SuccessMessage("Acceso a endpoint de administrador concedido")).build();
        }

        // Métodos auxiliares
        private String getClientIp(UriInfo uriInfo) {
                // En producción, deberías obtener esto de los headers (X-Forwarded-For, etc.)
                return "unknown";
        }

        private String getUserAgent() {
                // En producción, deberías obtener esto del header User-Agent
                return "unknown";
        }

        // DTOs internos
        public record ErrorMessage(String error) {}
        public record SuccessMessage(String message) {}
}
