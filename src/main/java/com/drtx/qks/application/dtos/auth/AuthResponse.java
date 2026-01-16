package com.drtx.qks.application.dtos.auth;
public record AuthResponse(
        String token,
        String type,
        Long expiresIn,
        UserInfo user
) {
    public AuthResponse(String token, Long expiresIn, UserInfo user) {
        this(token, "Bearer", expiresIn, user);
    }
}
