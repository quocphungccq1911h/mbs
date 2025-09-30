package com.ecommerce.service.mapper;

import com.ecommerce.model.UsersDO;
import com.ecommerce.service.dto.request.CreateUserRequest;
import com.ecommerce.service.dto.request.UpdateUserRequest;
import com.ecommerce.service.dto.response.UserResponse;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    // Request -> DO
    UsersDO toDO(CreateUserRequest request);

    // Update -> DO (update vào object sẵn có)
    void updateDO(UpdateUserRequest request, UsersDO usersDO);

    // DO -> Response
    UserResponse toResponse(UsersDO usersDO);
}

