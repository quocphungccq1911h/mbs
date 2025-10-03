package com.ecommerce.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

public class JwtUtil {
    private final Key signingKey;
    private final long accessExpMs;
    private final long refreshExpMs;
    public JwtUtil(String secret, long accessExpMs, long refreshExpMs) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.accessExpMs = accessExpMs;
        this.refreshExpMs = refreshExpMs;
    }

    /**
     * Hàm tạo access token
     *
     * @param userName
     * @param role
     * @return
     */
    public String generateAccessToken(String userName, String role) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(userName)
                .claim("role", role)
                .claim("type", "access")
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + accessExpMs))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Hàm tạo refresh token
     *
     * @param userName
     * @param jti
     * @return
     */
    public String generateRefreshToken(String userName, String jti) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(userName)
                .claim("type", "refresh")
                .setId(jti)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + refreshExpMs))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    private boolean isEncrypted(String s) {
        // simple heuristic: your encrypted value pattern, adapt as needed
        return s.startsWith("ENC(") && s.endsWith(")");
    }

    private Jws<Claims> parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token);
    }

    private String decryptSecret(String enc, String masterKey) {
        // enc format: ENC(base64cipher)
        try {
            String inner = enc.substring(4, enc.length() - 1);
            // demo AES decrypt using masterKey (must be 16/24/32 bytes)
            byte[] key = masterKey.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec skey = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skey);
            byte[] decoded = java.util.Base64.getDecoder().decode(inner);
            byte[] plain = cipher.doFinal(decoded);
            return new String(plain, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to decrypt JWT secret", ex);
        }
    }

    public boolean isAccessToken(String token) {
        try {
            Claims c = parseToken(token).getBody();
            return "access".equals(c.get("type"));
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isRefreshToken(String token) {
        try {
            Claims c = parseToken(token).getBody();
            return "refresh".equals(c.get("type"));
        } catch (Exception e) {
            return false;
        }
    }

    public String extractUsername(String token) {
        return parseToken(token).getBody().getSubject();
    }

    public String extractJti(String refreshToken) {
        return parseToken(refreshToken).getBody().getId();
    }

    public long getAccessExpMs() {
        return accessExpMs;
    }

    public long getRefreshExpMs() {
        return refreshExpMs;
    }

}
