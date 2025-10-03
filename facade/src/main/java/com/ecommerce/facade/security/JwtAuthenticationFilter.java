package com.ecommerce.facade.security;

import com.ecommerce.common.security.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final List<RequestMatcher> skipMatchers;

    // publicPaths có thể lấy từ cấu hình nếu muốn
    private static final String[] PUBLIC_URLS = new String[]{
            "/auth/**",
            "/api/users",        // POST register
            "/api/users/ping",   // health
            "/v3/api-docs/**",
            "/swagger-ui/**"
    };

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.skipMatchers = Arrays.stream(PUBLIC_URLS)
                .map(AntPathRequestMatcher::new)
                .collect(Collectors.toList());

    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // skip public paths
        for (RequestMatcher m : skipMatchers) {
            if (m.matches(request)) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        // Read token from header (support Authorization: Bearer ... OR X-Auth-Token)
        String authHeader = request.getHeader("Authorization");
        String token = null;
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        } else {
            // fallback header (some clients use X-Auth-Token)
            String alt = request.getHeader("X-Auth-Token");
            if (alt != null && !alt.isBlank()) token = alt;
        }

        // 3) No token -> let request proceed (public or will be blocked by security rules later)
        if (token == null || token.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 4) Validate token (your JwtUtil should throw or return false on invalid/expired)
            if (!jwtUtil.isAccessToken(token)) {
                // token present but invalid -> 401
                unauthorized(response, "Invalid or expired access token");
                return;
            }

            String username = jwtUtil.extractUsername(token);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                var authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
            // 5) continue the chain
            filterChain.doFilter(request, response);
        } catch (io.jsonwebtoken.JwtException | IllegalArgumentException ex) {
            // JWT lib exception (signature, malformed, expired, etc.)
            unauthorized(response, "Invalid or expired token");
        }
    }

    private void unauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("{\"error\":\"" + message + "\"}");
        response.getWriter().flush();
    }
}
