package com.drtx.qks.adapters.out.persistence;

import com.drtx.qks.domain.model.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class UserPanacheRepository implements PanacheRepository<UserEntity> {
    public Optional<UserEntity> findByUuid(UUID uuid) {
        return find("uuid", uuid).firstResultOptional();
    }
}
