package com.ecommerce.service.mapper;

import com.ecommerce.model.UsersDO;
import com.ecommerce.service.dto.request.CreateUserRequest;
import com.ecommerce.service.dto.request.UpdateUserRequest;
import com.ecommerce.service.dto.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    // Request -> DO
    UsersDO toDO(CreateUserRequest request);

    // Update -> DO (update vào object sẵn có)
    void updateDO(UpdateUserRequest request, @MappingTarget UsersDO usersDO);

    // DO -> Response
    UserResponse toResponse(UsersDO usersDO);
}

