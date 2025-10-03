package com.ecommerce.facade.controller;

import com.ecommerce.common.security.JwtUtil;
import com.ecommerce.dto.request.LoginRequest;
import com.ecommerce.facade.security.JwtConfig;
import com.ecommerce.facade.service.RefreshTokenService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtUtil jwtUtil,
                          RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest body,
                                   @RequestHeader(value = "User-Agent", required = false) String ua,
                                   @RequestHeader(value = "X-Forwarded-For", required = false) String ip) {

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(body.getUsername(), body.getPassword())
        );

        UserDetails user = (UserDetails) auth.getPrincipal();
        String role = user.getAuthorities()
                .stream()
                .findFirst()
                .map(Object::toString)
                .orElse("USER");

        String accessToken = jwtUtil.generateAccessToken(user.getUsername(), role);

        // create refresh token record in DB -> jti, then generate refresh token jwt with jti
        String jti = refreshTokenService.createRefreshTokenRecord(user.getUsername(), jwtUtil.getRefreshExpMs(), ua, ip);
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername(), jti);

        ResponseCookie cookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/auth")
                .maxAge(jwtUtil.getRefreshExpMs() / 1000)
                .sameSite("Strict")
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(Map.of("accessToken", accessToken)); // Access token returned in body, refresh in cookie
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@CookieValue(name = "refresh_token", required = false) String refreshToken,
                                     @RequestHeader(value = "User-Agent", required = false) String ua) {
        if (refreshToken == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No refresh token");

        try {
            if (!jwtUtil.isRefreshToken(refreshToken))
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token type");

            String username = jwtUtil.extractUsername(refreshToken);
            String jti = jwtUtil.extractJti(refreshToken);

            boolean valid = refreshTokenService.validateJti(jti, username);
            if (!valid) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token invalid");

            // issue new access token (and rotate refresh token if desired)
            String accessToken = jwtUtil.generateAccessToken(username, "USER");

            // Optionally rotate refresh token:
            // revoke old jti, create new jti, insert, generate new refresh JWT, set cookie
            refreshTokenService.revoke(jti);
            String newJti = refreshTokenService.createRefreshTokenRecord(username, jwtUtil.getRefreshExpMs(), ua, null);
            String newRefreshToken = jwtUtil.generateRefreshToken(username, newJti);

            ResponseCookie cookie = ResponseCookie.from("refresh_token", newRefreshToken)
                    .httpOnly(true)
                    .secure(true)
                    .path("/auth")
                    .maxAge(jwtUtil.getRefreshExpMs() / 1000)
                    .sameSite("Strict")
                    .build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(Map.of("accessToken", accessToken));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@CookieValue(name = "refresh_token", required = false) String refreshToken) {
        if (refreshToken != null && jwtUtil.isRefreshToken(refreshToken)) {
            String jti = jwtUtil.extractJti(refreshToken);
            refreshTokenService.revoke(jti);
        }

        ResponseCookie cookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(true)
                .path("/auth")
                .maxAge(0)
                .sameSite("Strict")
                .build();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body("Logged out");
    }
}
