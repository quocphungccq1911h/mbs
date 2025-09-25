package com.ecommerce.service.user;

import com.ecommerce.service.dto.request.CreateUserRequest;
import com.ecommerce.service.dto.request.UpdateUserRequest;
import com.ecommerce.service.dto.response.UserResponse;

public interface UserService {
    UserResponse createUser(CreateUserRequest request);

    UserResponse updateUser(Long userId, UpdateUserRequest request);

    UserResponse getUser(Long userId);
}
