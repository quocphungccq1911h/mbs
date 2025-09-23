package com.ecommerce.repository.mapper.ext;

import com.ecommerce.model.UsersDO;

public interface UserDOExtMapper {
    UsersDO selectByUsername(String username);
}
