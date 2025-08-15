package com.berryweb.shop.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            String jwt = getJwtFromRequest(request);
            log.debug("JWT token: {}", jwt != null ? "present" : "not present");

            if (jwt != null) {
                log.debug("Token length: {}, prefix: {}", jwt.length(),
                        jwt.length() > 20 ? jwt.substring(0, 20) + "..." : jwt);
            }

            if (jwt != null && tokenProvider.validateToken(jwt)) {
                Long userId = null;

                try {
                    userId = tokenProvider.getUserIdFromToken(jwt);
                    log.debug("Extracted user ID: {} (type: {})", userId,
                            userId != null ? userId.getClass().getSimpleName() : "null");
                } catch (ClassCastException e) {
                    log.error("ClassCastException while extracting user ID from token: {}", e.getMessage(), e);
                    // 토큰이 유효하지만 userId 추출 실패시 인증 없이 진행
                } catch (Exception e) {
                    log.error("Unexpected error while extracting user ID from token: {}", e.getMessage(), e);
                }

                if (userId != null) {
                    try {
                        // principal로 Long 타입의 userId 설정
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);

                        log.debug("Authentication set successfully for user ID: {}", userId);
                    } catch (Exception e) {
                        log.error("Error setting authentication for user ID {}: {}", userId, e.getMessage(), e);
                        SecurityContextHolder.clearContext();
                    }
                } else {
                    log.warn("Failed to extract user ID from valid JWT token");
                }
            } else {
                if (jwt != null) {
                    log.debug("JWT token validation failed");
                } else {
                    log.debug("No JWT token found in request");
                }
            }
        } catch (Exception e) {
            log.error("Unexpected error during JWT authentication: {}", e.getMessage(), e);
            // 인증 실패 시 SecurityContext 클리어
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);
            log.debug("Extracted JWT token from Authorization header");
            return token;
        }
        return null;
    }

}
