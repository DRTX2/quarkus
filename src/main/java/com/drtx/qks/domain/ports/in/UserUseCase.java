package com.drtx.qks.domain.ports.in;

import com.drtx.qks.domain.model.User;
import com.drtx.qks.domain.valueObjects.Page;
import com.drtx.qks.domain.valueObjects.UserFilter;

import java.util.Optional;

// use case with methods for a user crud with pagination and filtering
public interface UserUseCase {
    Page<User> findAll(
            UserFilter filter,
            int page,
            int size
    );
    Optional<User> findById(Long id);
    User update(User user);
    void deleteById(Long id);
}
