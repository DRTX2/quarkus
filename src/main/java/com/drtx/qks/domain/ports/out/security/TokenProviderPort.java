package com.drtx.qks.domain.ports.out.security;

import com.drtx.qks.domain.model.User;

public interface TokenProviderPort {
    String generateToken(String subject);
    boolean validateToken(String token);
    String getSubjectFromToken(String token);
    String getAccessToken(User user);
    String refreshAccessToken(User user);
}
