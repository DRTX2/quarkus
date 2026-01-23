package com.drtx.qks.application.usecase;

import com.drtx.qks.domain.model.User;
import com.drtx.qks.domain.ports.in.user.UserUseCase;
import com.drtx.qks.domain.ports.out.persistence.UserRepositoryPort;
import com.drtx.qks.domain.valueObjects.Page;
import com.drtx.qks.domain.valueObjects.UserFilter;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class UserUseCaseImpl implements UserUseCase {

    @Inject
    UserRepositoryPort userRepository;

    @Override
    public Page<User> findAll(UserFilter filter, int page, int size) {
        return userRepository.findAll(filter, page, size);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> findByUuid(UUID uuid) {
        return userRepository.findByUuid(uuid);
    }

    @Override
    public User update(User user) {
        return userRepository.save(user);
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public void deleteByUuid(UUID uuid) {
        Optional<User> userOpt = userRepository.findByUuid(uuid);
        if(userOpt.isEmpty()) throw new IllegalArgumentException("User not found with uuid: " + uuid);
        userOpt.ifPresent(user -> userRepository.deleteById(user.getId()));
    }
}
