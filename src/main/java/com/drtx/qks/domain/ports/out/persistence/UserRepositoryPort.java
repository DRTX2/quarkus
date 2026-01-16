package com.drtx.qks.domain.ports.out.persistence;

import com.drtx.qks.domain.model.User;
import com.drtx.qks.domain.valueObjects.Page;
import com.drtx.qks.domain.valueObjects.UserFilter;

import java.util.List;
import java.util.Optional;

public interface UserRepositoryPort {
    User save(User user);
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    Page<User> findAll(UserFilter filter, int page, int size);
    void deleteById(Long id);
}
