package com.drtx.qks.domain.ports.in;

import com.drtx.qks.domain.model.User;

public interface AuthenticationUseCase {
    User register(User user);
    User authenticate(String username, String password);
}
