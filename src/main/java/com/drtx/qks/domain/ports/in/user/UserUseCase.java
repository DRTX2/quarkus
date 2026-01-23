package com.drtx.qks.domain.ports.in.user;

import com.drtx.qks.domain.model.User;
import com.drtx.qks.domain.valueObjects.Page;
import com.drtx.qks.domain.valueObjects.UserFilter;

import java.util.Optional;
import java.util.UUID;

public interface UserUseCase {
    Page<User> findAll(
            UserFilter filter,
            int page,
            int size
    );
    Optional<User> findById(Long id);
    Optional<User> findByUuid(UUID uuid);
    User update(User user);
    void deleteById(Long id);
    void deleteByUuid(UUID uuid);
}
