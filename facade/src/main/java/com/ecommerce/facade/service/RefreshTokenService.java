package com.ecommerce.facade.service;

import com.ecommerce.model.RefreshTokenDO;
import com.ecommerce.model.RefreshTokenDOExample;
import com.ecommerce.repository.mapper.RefreshTokenDOMapper;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

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

    public boolean validateJti(String jti, String userName) {
        RefreshTokenDOExample example = new RefreshTokenDOExample();
        example.createCriteria()
                .andJtiEqualTo(jti);

        List<RefreshTokenDO> tokens = refreshTokenDOMapper.selectByExample(example);
        if (tokens.isEmpty()) {
            return false;
        }
        RefreshTokenDO token = tokens.get(0);

        if (!userName.equals(token.getUsername())) return false;
        if (token.getRevoked() != null && token.getRevoked()) return false;
        if (token.getExpiresAt() == null || token.getExpiresAt().before(new Date())) return false;
        return true;
    }

    public  String createRefreshTokenRecord(String userName, long expiresMs, String device, String ip) {
        String jti = UUID.randomUUID().toString();
        RefreshTokenDO token = new RefreshTokenDO();
        token.setJti(jti);
        token.setUsername(userName);
        token.setIssuedAt(new Date());
        token.setExpiresAt(new Date(System.currentTimeMillis() + expiresMs));
        token.setRevoked(false);
        token.setDevice(device);
        token.setIp(ip);
        refreshTokenDOMapper.insert(token);
        return jti;
    }
}
