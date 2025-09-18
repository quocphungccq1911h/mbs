package com.ecommerce.repository.mapper.ext;

import com.ecommerce.model.RefreshTokenDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RefreshTokenDOExtMapper {
    List<RefreshTokenDO> findValidTokensByUserId(@Param("userId") Long userId);
    int deleteByUserId(@Param("userId") Long userId);
}
