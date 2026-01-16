package com.drtx.qks.application.mappers;

import com.drtx.qks.application.dtos.auth.UserInfo;
import com.drtx.qks.domain.model.User;
import org.mapstruct.Mapper;

/**
 * Mapper para convertir entre User y UserInfo
 */
@Mapper(componentModel = "cdi")
public interface UserInfoMapper {
    UserInfo toUserInfo(User user);
}

