package com.ecommerce.service.mapper;

import com.ecommerce.model.UsersDO;
import com.ecommerce.service.dto.request.CreateUserRequest;
import com.ecommerce.service.dto.request.UpdateUserRequest;
import com.ecommerce.service.dto.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    // Request -> DO
    UsersDO toDO(CreateUserRequest request);

    // Update -> DO (update vào object sẵn có)
    @Mapping(target = "id", ignore = true) // ví dụ bỏ qua id khi update
    void updateDO(UpdateUserRequest request, @MappingTarget UsersDO usersDO);

    // DO -> Response
    UserResponse toResponse(UsersDO usersDO);
}

