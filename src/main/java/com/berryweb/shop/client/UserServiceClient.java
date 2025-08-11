package com.berryweb.shop.client;

import com.berryweb.shop.dto.UserServiceDto;
import jakarta.persistence.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
        name = "user-service",
        url = "${services.user-service.url}",
        fallback = UserServiceClientFallback.class
)
public interface UserServiceClient {

    @GetMapping("/api/v1/users/{userId}")
    @Cacheable(value = "users", key = "#userId")
    UserServiceDto.ApiResponse<UserServiceDto.UserInfo> getUserInfo(
            @PathVariable("userId") Long userId,
            @RequestHeader("Authorization") String token
    );

}
