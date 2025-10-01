package com.ecommerce.service.user.impl;

import com.ecommerce.model.UsersDO;
import com.ecommerce.repository.mapper.UsersDOMapper;
import com.ecommerce.service.dto.request.CreateUserRequest;
import com.ecommerce.service.dto.request.UpdateUserRequest;
import com.ecommerce.service.dto.response.UserResponse;
import com.ecommerce.service.mapper.UserMapper;
import com.ecommerce.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UsersDOMapper usersDOMapper;
    private final UserMapper userMapper;
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        UsersDO usersDO = userMapper.toDO(request);
        usersDOMapper.insertSelective(usersDO);
        return userMapper.toResponse(usersDO);
    }

    @Transactional
    public UserResponse updateUser(Long userId, UpdateUserRequest request) {
        UsersDO usersDO = usersDOMapper.selectByPrimaryKey(userId);
        if (usersDO == null)
        {
            log.warn("updateUser not found user: {}", request);
        }
        userMapper.updateDO(request, usersDO);
        usersDOMapper.updateByPrimaryKeySelective(usersDO);
        return userMapper.toResponse(usersDO);
    }

    public UserResponse getUser(Long userId) {
        UsersDO usersDO = usersDOMapper.selectByPrimaryKey(userId);
        if (usersDO == null)
        {
            log.warn("getUser not found userId: {}", userId);
        }
        return userMapper.toResponse(usersDO);
    }

    @Override
    public Boolean deleteUserById(Long userId) {
        UsersDO usersDO = usersDOMapper.selectByPrimaryKey(userId);
        if (usersDO == null)
        {
            log.warn("deleteUserById not found userId: {}", userId);
        }
        return usersDOMapper.deleteByPrimaryKey(userId) > 0;
    }
}
