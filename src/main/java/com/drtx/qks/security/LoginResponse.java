package com.drtx.qks.security;

/**
 * DTO para respuesta de login con token
 */
public class LoginResponse {
    public String token;
    public String type = "Bearer";
    public Long expiresIn;

    public LoginResponse() {
    }

    public LoginResponse(String token, Long expiresIn) {
        this.token = token;
        this.expiresIn = expiresIn;
    }
}

