package com.ecommerce.service.mapper.impl;

import com.ecommerce.model.UsersDO;
import com.ecommerce.service.dto.request.CreateUserRequest;
import com.ecommerce.service.dto.request.UpdateUserRequest;
import com.ecommerce.service.dto.response.UserResponse;
import com.ecommerce.service.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserMapperImpl implements UserMapper {

    private final PasswordEncoder passwordEncoder;

    @Override
    public UsersDO toDO(CreateUserRequest request) {
        if (request == null)
            return null;

        UsersDO usersDO = new UsersDO();

        usersDO.setUsername(request.getUsername());
        usersDO.setUserPassword(passwordEncoder.encode(request.getUserPassword()));
        usersDO.setEmail(request.getEmail());
        usersDO.setFullName(request.getFullName());

        return usersDO;
    }

    @Override
    public void updateDO(UpdateUserRequest request, UsersDO usersDO) {
        if ( request == null ) {
            return;
        }

        usersDO.setEmail( request.getEmail() );
        usersDO.setFullName( request.getFullName() );
        usersDO.setUserStatus( request.getUserStatus() );
    }

    @Override
    public UserResponse toResponse(UsersDO usersDO) {
        if(usersDO == null) {
            return null;
        }
        UserResponse response = new UserResponse();
        response.setId(usersDO.getId());
        response.setUsername(usersDO.getUsername());
        response.setFullName(usersDO.getFullName());
        response.setUserStatus(usersDO.getUserStatus());
        response.setEmail(usersDO.getEmail());
        return response;
    }
}
