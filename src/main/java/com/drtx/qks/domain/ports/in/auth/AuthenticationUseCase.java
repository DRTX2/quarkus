package com.drtx.qks.domain.ports.in.auth;

import com.drtx.qks.domain.model.User;

public interface AuthenticationUseCase {

 String hashPassword(String rawPassword);

 boolean validatePassword(String rawPassword, String hashedPassword);

 void changePassword(Long userId, String currentPassword, String newPassword);

 User authenticate(String usernameOrEmail, String password);

 User register(String username, String email, String password);
}