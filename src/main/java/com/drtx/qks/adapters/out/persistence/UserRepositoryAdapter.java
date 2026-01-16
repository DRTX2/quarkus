package com.drtx.qks.adapters.out.persistence;

import com.drtx.qks.domain.model.User;
import com.drtx.qks.domain.ports.out.persistence.UserRepositoryPort;
import com.drtx.qks.domain.valueObjects.Page;
import com.drtx.qks.domain.valueObjects.UserFilter;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
public class UserRepositoryAdapter implements UserRepositoryPort {

    @Inject
    UserPanacheRepository userPanacheRepository;
    @Inject
    UserPersistenceMapper userPersistenceMapper;

    @Override
    public User save(User user) {
        UserEntity entity= userPersistenceMapper.toEntity(user);
        userPanacheRepository.persist(entity);
        return userPersistenceMapper.toDomain(entity);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userPanacheRepository.findByIdOptional(id)
                .map(userPersistenceMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userPanacheRepository.find("email", email)
                .firstResultOptional()
                .map(userPersistenceMapper::toDomain);
    }

    @Override
    public Page<User> findAll(UserFilter filter, int page, int size) {
        var query = userPanacheRepository.find(buildQueryString(filter), buildQueryParams(filter));
        query.page(io.quarkus.panache.common.Page.of(page, size));

        var content = query.list()
                .stream()
                .map(userPersistenceMapper::toDomain)
                .toList();

        long totalElements = query.count();
        int totalPages = (int) Math.ceil((double) totalElements / size);

        return new Page<>(content, page, size, totalElements, totalPages);
    }

    // query builder helper methods

    private String buildQueryString(UserFilter filter) {
        StringBuilder queryBuilder = new StringBuilder("1=1");
        if (filter.username() != null) {
            queryBuilder.append(" and username like :username");
        }
        if (filter.email() != null) {
            queryBuilder.append(" and email like :email");
        }
        if (filter.role() != null) {
            queryBuilder.append(" and role = :role");
        }
        if (filter.enabled() != null) {
            queryBuilder.append(" and enabled = :enabled");
        }
        return queryBuilder.toString();
    }

    private Map<String, Object> buildQueryParams(UserFilter filter) {
        var params = new HashMap<String, Object>();
        if (filter.username() != null) {
            params.put("username", "%" + filter.username() + "%");
        }
        if (filter.email() != null) {
            params.put("email", "%" + filter.email() + "%");
        }
        if (filter.role() != null) {
            params.put("role", filter.role());
        }
        if (filter.enabled() != null) {
            params.put("enabled", filter.enabled());
        }
        return params;
    }

    @Override
    public void deleteById(Long id) {
        userPanacheRepository.deleteById(id);
    }


}
