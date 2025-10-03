package com.ecommerce.facade.security;

import com.ecommerce.common.security.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {
    @Value("${jwt.secret}")
    private String jwtSecretRaw;

    @Value("${jwt.master-key:}")
    private String masterKey; // optional: nếu jwtSecretRaw đang được mã hóa

    @Value("${jwt.access-exp-ms:900000}")
    private long accessExpMs;

    @Value("${jwt.refresh-exp-ms:604800000}")
    private long refreshExpMs;

    @Bean
    public JwtUtil jwtUtil() {
        return new JwtUtil(jwtSecretRaw, accessExpMs, refreshExpMs);
    }
}
