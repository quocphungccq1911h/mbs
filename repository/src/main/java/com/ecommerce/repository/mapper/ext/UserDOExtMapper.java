package com.ecommerce.repository.mapper.ext;

import com.ecommerce.model.UserDO;

public interface UserDOExtMapper {
    UserDO selectByUsername(String username);
}
