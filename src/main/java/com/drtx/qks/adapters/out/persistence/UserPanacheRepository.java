package com.drtx.qks.adapters.out.persistence;

import com.drtx.qks.domain.model.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserPanacheRepository implements PanacheRepository<UserEntity> {
}
