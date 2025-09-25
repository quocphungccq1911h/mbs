package com.ecommerce.service.user.impl;

import com.ecommerce.model.UsersDO;
import com.ecommerce.repository.mapper.UsersDOMapper;
import com.ecommerce.service.dto.request.CreateUserRequest;
import com.ecommerce.service.dto.request.UpdateUserRequest;
import com.ecommerce.service.dto.response.UserResponse;
import com.ecommerce.service.mapper.UserMapper;
import com.ecommerce.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UsersDOMapper usersDOMapper;
    private final UserMapper userMapper;

    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        UsersDO usersDO = userMapper.toDO(request);
        usersDOMapper.insertSelective(usersDO);
        return userMapper.toResponse(usersDO);
    }

    @Transactional
    public UserResponse updateUser(Long userId, UpdateUserRequest request) {
        UsersDO usersDO = usersDOMapper.selectByPrimaryKey(userId);
        if (usersDO == null) throw new RuntimeException("User not found");
        userMapper.updateDO(request, usersDO);
        usersDOMapper.updateByPrimaryKeySelective(usersDO);
        return userMapper.toResponse(usersDO);
    }

    public UserResponse getUser(Long userId) {
        UsersDO usersDO = usersDOMapper.selectByPrimaryKey(userId);
        if (usersDO == null) throw new RuntimeException("User not found");
        return userMapper.toResponse(usersDO);
    }
}
