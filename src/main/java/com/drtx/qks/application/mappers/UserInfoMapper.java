package com.drtx.qks.application.mappers;

import com.drtx.qks.application.dtos.auth.UserInfo;
import com.drtx.qks.domain.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "cdi")
public interface UserInfoMapper {
    @Mapping(source = "uuid", target = "id")
    UserInfo toUserInfo(User user);
}
