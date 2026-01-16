package com.drtx.qks.adapters.out.persistence;

import com.drtx.qks.domain.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface UserPersistenceMapper {
    UserEntity toEntity(User user);
    User toDomain(UserEntity userEntity);
}
