package com.berryweb.shop.service;

import com.berryweb.shop.dto.UserServiceDto;
import jakarta.persistence.Cacheable;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.http.HttpHeaders;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceClient {

    private final RestTemplate restTemplate;

    @Value("${services.user-service.url}")
    private String userServiceUrl;

    @Cacheable(value = "users", key = "#userId")
    public UserServiceDto.UserInfo getUserInfo(Long userId, String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<UserServiceDto.ApiResponse> response = restTemplate.exchange(
                    userServiceUrl + "/api/v1/users/" + userId,
                    HttpMethod.GET,
                    entity,
                    UserServiceDto.ApiResponse.class
            );

            if (response.getBody() != null && response.getBody().isSuccess()) {
                Map<String, Object> data = (Map<String, Object>) response.getBody().getData();
                return UserServiceDto.UserInfo.builder()
                        .id(((Number) data.get("id")).longValue())
                        .username((String) data.get("username"))
                        .email((String) data.get("email"))
                        .name((String) data.get("name"))
                        .role(UserServiceDto.UserInfo.UserRole.valueOf((String) data.get("role")))
                        .isActive((Boolean) data.get("isActive"))
                        .build();
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
