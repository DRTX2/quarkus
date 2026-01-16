package com.drtx.qks.application.services.auth;

import com.drtx.qks.domain.exceptions.AuthenticationException;
import com.drtx.qks.domain.exceptions.BusinessException;
import com.drtx.qks.domain.model.User;
import com.drtx.qks.domain.ports.in.auth.AuthenticationUseCase;
import com.drtx.qks.domain.ports.out.persistence.UserRepositoryPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDateTime;
import java.util.Set;

@ApplicationScoped
public class AuthenticationService implements AuthenticationUseCase {

    @Inject
    UserRepositoryPort userRepository;

    @Override
    @Transactional
    public User register(String username, String email, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new BusinessException("El username ya esta en uso");
        }

        if (userRepository.findByEmail(email).isPresent()) {
            throw new BusinessException("El email ya esta registrado");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(hashPassword(password));
        user.setRoles(Set.of("USER"));
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    @Override
    public User authenticate(String usernameOrEmail, String password) {
        User user = userRepository.findByUsername(usernameOrEmail)
                .or(() -> userRepository.findByEmail(usernameOrEmail))
                .orElseThrow(() -> new AuthenticationException("Credenciales invalidas"));

        if (!user.isEnabled()) {
            throw new AuthenticationException("Usuario deshabilitado");
        }

        if (!validatePassword(password, user.getPasswordHash())) {
            throw new AuthenticationException("Credenciales invalidas");
        }

        return user;
    }

    @Override
    @Transactional
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("Usuario no encontrado"));

        if (!validatePassword(currentPassword, user.getPasswordHash())) {
            throw new AuthenticationException("Password actual incorrecto");
        }

        user.setPasswordHash(hashPassword(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    public boolean validatePassword(String rawPassword, String hashedPassword) {
        try {
            return BCrypt.checkpw(rawPassword, hashedPassword);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String hashPassword(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt(12));
    }
}