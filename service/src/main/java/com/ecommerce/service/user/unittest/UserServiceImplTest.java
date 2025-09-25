package com.ecommerce.service.user.unittest;

import com.ecommerce.model.UsersDO;
import com.ecommerce.repository.mapper.UsersDOMapper;
import com.ecommerce.service.dto.request.CreateUserRequest;
import com.ecommerce.service.dto.request.UpdateUserRequest;
import com.ecommerce.service.dto.response.UserResponse;
import com.ecommerce.service.mapper.UserMapper;
import com.ecommerce.service.user.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserServiceImplTest {
    @Mock
    private UsersDOMapper usersDOMapper;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private UsersDO userDO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userDO = new UsersDO();
        userDO.setId(1L);
        userDO.setUsername("quocphungccq1911h");
    }

    @Test
    void createUser_success() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("quocphungccq1911h");

        when(userMapper.toDO(request)).thenReturn(userDO);
        when(userMapper.toResponse(userDO)).thenReturn(new UserResponse(1L, "quocphungccq1911h", "Phan", "ACTIVE", "quocphung@gmail.com"));

        UserResponse response = userService.createUser(request);

        assertNotNull(response);
        assertEquals("quocphungccq1911h", response.getUsername());
        verify(usersDOMapper, times(1)).insertSelective(userDO);
    }

    @Test
    void updateUser_success() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setFullName("Phan Phung");

        when(usersDOMapper.selectByPrimaryKey(1L)).thenReturn(userDO);
        doAnswer(invocationOnMock -> {
            userDO.setFullName("Phan Phung");
            return null;
        }).when(userMapper).updateDO(request, userDO);
        when(userMapper.toResponse(userDO)).thenReturn(new UserResponse(1L, "quocphungccq1911h", "Phan Phung", "ACTIVE", "quocphung@gmail.com"));

        UserResponse response = userService.updateUser(1L, request);

        assertEquals("Phan Phung", response.getFullName());
        verify(usersDOMapper, times(1)).updateByPrimaryKeySelective(userDO);
    }
}
