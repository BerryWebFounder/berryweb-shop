package com.berryweb.shop.client;

import com.berryweb.shop.dto.UserServiceDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserServiceClientFallbackFactory implements FallbackFactory<UserServiceClient> {

    @Override
    public UserServiceClient create(Throwable cause) {
        return new UserServiceClient() {
            @Override
            public UserServiceDto.ApiResponse<UserServiceDto.UserInfo> getUserInfo(Long userId, String token) {
                log.warn("User service is unavailable for userId: {}. Cause: {}", userId, cause.getMessage());

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
                        .message("Fallback response due to: " + cause.getMessage())
                        .build();
            }
        };
    }

}
