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

    @Cacheable(value = "users", key = "#userId")
    public UserServiceDto.UserInfo getUserInfo(Long userId, String token) {
        try {
            // token에 "Bearer " 접두사가 없으면 추가
            String authToken = token.startsWith("Bearer ") ? token : "Bearer " + token;

            UserServiceDto.ApiResponse<UserServiceDto.UserInfo> response =
                    userServiceClient.getUserInfo(userId, authToken);

            if (response != null && response.isSuccess() && response.getData() != null) {
                return response.getData();
            }
        } catch (Exception e) {
            log.error("Failed to get user info for userId: {}", userId, e);
        }
        return null;
    }

    public List<UserServiceDto.UserInfo> getUsersInfo(List<Long> userIds, String token) {
        return userIds.stream()
                .map(userId -> getUserInfo(userId, token))
                .filter(userInfo -> userInfo != null)
                .toList();
    }

}
