package com.ecommerce.facade.service;

import com.ecommerce.model.RefreshTokenDO;
import com.ecommerce.model.RefreshTokenDOExample;
import com.ecommerce.repository.mapper.RefreshTokenDOMapper;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenService {
    private final RefreshTokenDOMapper refreshTokenDOMapper;

    public RefreshTokenService(RefreshTokenDOMapper refreshTokenDOMapper) {
        this.refreshTokenDOMapper = refreshTokenDOMapper;
    }

    public void delete(String jti) {
        RefreshTokenDOExample example = new RefreshTokenDOExample();
        example.createCriteria()
                .andJtiEqualTo(jti);
        refreshTokenDOMapper.deleteByExample(example);
    }

    public void revoke(String jti) {
        // Tạo đối tượng cập nhật
        RefreshTokenDO record = new RefreshTokenDO();
        record.setRevoked(true);

        // Tạo điều kiện where
        RefreshTokenDOExample example = new RefreshTokenDOExample();
        example.createCriteria()
                .andJtiEqualTo(jti);

        // Gọi updateByExampleSelective => chỉ update field revoked
        refreshTokenDOMapper.updateByExampleSelective(record, example);

    }
}
