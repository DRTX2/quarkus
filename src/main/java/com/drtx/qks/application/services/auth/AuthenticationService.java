package com.drtx.qks.application.services.auth;

import com.drtx.qks.domain.exceptions.AuthenticationException;
import com.drtx.qks.domain.exceptions.BusinessException;
import com.drtx.qks.domain.model.AuthAuditLog;
import com.drtx.qks.domain.model.User;
import com.drtx.qks.domain.ports.in.auth.AuthenticationUseCase;
import com.drtx.qks.domain.ports.out.audit.AuthAuditPort;
import com.drtx.qks.domain.ports.out.persistence.UserRepositoryPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * Servicio de autenticación con seguridad mejorada
 */
@ApplicationScoped
public class AuthenticationService implements AuthenticationUseCase {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);

    @ConfigProperty(name = "security.max-login-attempts", defaultValue = "5")
    int maxLoginAttempts;

    @ConfigProperty(name = "security.account-lock-duration", defaultValue = "900") // 15 minutos
    long accountLockDuration;

    private final UserRepositoryPort userRepository;
    private final AuthAuditPort authAuditPort;

    @Inject
    public AuthenticationService(UserRepositoryPort userRepository, AuthAuditPort authAuditPort) {
        this.userRepository = userRepository;
        this.authAuditPort = authAuditPort;
    }

    @Override
    @Transactional
    public User register(String username, String email, String password) {
        log.info("Intentando registrar nuevo usuario: {}", username);

        if (userRepository.findByUsername(username).isPresent()) {
            throw new BusinessException("El username ya está en uso");
        }

        if (userRepository.findByEmail(email).isPresent()) {
            throw new BusinessException("El email ya está registrado");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(hashPassword(password));
        user.setRoles(Set.of(com.drtx.qks.domain.constants.RoleEnum.USER.getRoleName()));
        user.setEnabled(true);
        user.setLocked(false);
        user.setFailedLoginAttempts(0);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);
        log.info("Usuario registrado exitosamente: {}", username);

        return savedUser;
    }

    @Override
    @Transactional
    public User authenticate(String usernameOrEmail, String password) {
        log.info("Intento de autenticación para: {}", usernameOrEmail);

        User user = userRepository.findByUsername(usernameOrEmail)
                .or(() -> userRepository.findByEmail(usernameOrEmail))
                .orElseThrow(() -> {
                    authAuditPort.logLoginFailed(usernameOrEmail, null, null, "Usuario no encontrado");
                    return new AuthenticationException("Credenciales inválidas");
                });

        // Verificar si la cuenta está bloqueada
        if (user.isLocked()) {
            log.warn("Intento de login en cuenta bloqueada: {}", user.getUsername());
            throw new AuthenticationException("Cuenta bloqueada. Intente más tarde.");
        }

        if (!user.isEnabled()) {
            log.warn("Intento de login en cuenta deshabilitada: {}", user.getUsername());
            throw new AuthenticationException("Usuario deshabilitado");
        }

        // Validar password
        if (!validatePassword(password, user.getPasswordHash())) {
            handleFailedLogin(user);
            authAuditPort.logLoginFailed(user.getUsername(), null, null, "Password incorrecto");
            throw new AuthenticationException("Credenciales inválidas");
        }

        // Login exitoso
        handleSuccessfulLogin(user);
        authAuditPort.logLoginSuccess(user.getId(), user.getUsername(), null, null);

        log.info("Autenticación exitosa para usuario: {}", user.getUsername());
        return user;
    }

    @Override
    @Transactional
    public void changePassword(UUID userUuid, String currentPassword, String newPassword) {
        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new BusinessException("Usuario no encontrado"));

        if (!validatePassword(currentPassword, user.getPasswordHash())) {
            authAuditPort.logAuthEvent(new AuthAuditLog(
                user.getId(),
                user.getUsername(),
                AuthAuditLog.AuthEventType.PASSWORD_CHANGE,
                null,
                null,
                "Intento fallido - password actual incorrecto"
            ));
            throw new AuthenticationException("Password actual incorrecto");
        }

        user.setPasswordHash(hashPassword(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        authAuditPort.logPasswordChange(user.getId(), user.getUsername(), null, null);
        log.info("Password cambiado exitosamente para usuario: {}", user.getUsername());
    }

    @Override
    public boolean validatePassword(String rawPassword, String hashedPassword) {
        try {
            return BCrypt.checkpw(rawPassword, hashedPassword);
        } catch (Exception e) {
            log.error("Error validando password", e);
            return false;
        }
    }

    @Override
    public String hashPassword(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt(12));
    }

    /**
     * Maneja un intento de login fallido
     */
    private void handleFailedLogin(User user) {
        int attempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(attempts);

        if (attempts >= maxLoginAttempts) {
            user.setLocked(true);
            authAuditPort.logAuthEvent(new AuthAuditLog(
                user.getId(),
                user.getUsername(),
                AuthAuditLog.AuthEventType.ACCOUNT_LOCKED,
                null,
                null,
                "Cuenta bloqueada por múltiples intentos fallidos"
            ));
            log.warn("Cuenta bloqueada por intentos fallidos: {}", user.getUsername());
        }

        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    /**
     * Maneja un login exitoso
     */
    private void handleSuccessfulLogin(User user) {
        user.setFailedLoginAttempts(0);
        user.setLastLoginAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }
}

