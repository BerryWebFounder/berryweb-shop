package com.berryweb.shop.service;

import com.berryweb.shop.client.UserServiceClient;
import com.berryweb.shop.dto.UserServiceDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceHelper {

    private final UserServiceClient userServiceClient;

    @Cacheable(value = "users", key = "#userId", condition = "#userId != null")
    public UserServiceDto.UserInfo getUserInfo(Long userId, String token) {
        if (userId == null) {
            log.warn("getUserInfo called with null userId");
            return null;
        }

        if (token == null || token.trim().isEmpty()) {
            log.warn("getUserInfo called with null or empty token for userId: {}", userId);
            return null;
        }

        try {
            // token에 "Bearer " 접두사가 없으면 추가
            String authToken = token.startsWith("Bearer ") ? token : "Bearer " + token;

            log.debug("Calling User Service for userId: {} with token prefix: {}",
                    userId, authToken.substring(0, Math.min(20, authToken.length())) + "...");

            UserServiceDto.ApiResponse<UserServiceDto.UserInfo> response =
                    userServiceClient.getUserInfo(userId, authToken);

            if (response != null && response.isSuccess() && response.getData() != null) {
                log.debug("Successfully retrieved user info for userId: {}", userId);
                return response.getData();
            } else {
                log.warn("User Service returned unsuccessful response for userId: {}. Success: {}, Data: {}",
                        userId, response != null ? response.isSuccess() : "null",
                        response != null ? response.getData() : "null");
            }
        } catch (Exception e) {
            log.error("Failed to get user info for userId: {}", userId, e);
        }
        return null;
    }

    public List<UserServiceDto.UserInfo> getUsersInfo(List<Long> userIds, String token) {
        if (userIds == null || userIds.isEmpty()) {
            log.warn("getUsersInfo called with null or empty userIds");
            return List.of();
        }

        return userIds.stream()
                .filter(userId -> userId != null) // null userId 필터링
                .map(userId -> getUserInfo(userId, token))
                .filter(userInfo -> userInfo != null) // null 결과 필터링
                .toList();
    }

}
