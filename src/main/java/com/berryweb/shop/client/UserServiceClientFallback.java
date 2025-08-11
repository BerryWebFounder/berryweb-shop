package com.berryweb.shop.client;

import com.berryweb.shop.dto.UserServiceDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
public class UserServiceClientFallback implements UserServiceClient {

    @Override
    public UserServiceDto.ApiResponse<UserServiceDto.UserInfo> getUserInfo(Long userId, String token) {
        log.warn("User service is unavailable. Using fallback for userId: {}", userId);

        // 기본 사용자 정보 반환
        UserServiceDto.UserInfo defaultUser = UserServiceDto.UserInfo.builder()
                .id(userId)
                .username("알 수 없음")
                .email("unknown@example.com")
                .name("알 수 없음")
                .role(UserServiceDto.UserInfo.UserRole.USER)
                .isActive(true)
                .build();

        return UserServiceDto.ApiResponse.<UserServiceDto.UserInfo>builder()
                .success(true)
                .data(defaultUser)
                .message("Fallback response")
                .build();
    }

}
