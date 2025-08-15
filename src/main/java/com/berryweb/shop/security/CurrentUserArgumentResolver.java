package com.berryweb.shop.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@Slf4j
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUser.class) &&
                parameter.getParameterType().equals(Long.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            log.debug("No authentication found or not authenticated");
            return null;
        }

        Object principal = authentication.getPrincipal();
        log.debug("Principal type: {}, value: {}",
                principal != null ? principal.getClass().getSimpleName() : "null", principal);

        if (principal == null) {
            log.warn("Principal is null");
            return null;
        }

        // String "anonymousUser"는 익명 사용자이므로 null 반환
        if ("anonymousUser".equals(principal)) {
            log.debug("Anonymous user detected");
            return null;
        }

        try {
            // principal이 Long 타입인 경우
            if (principal instanceof Long) {
                Long userId = (Long) principal;
                log.debug("Successfully resolved user ID from Long principal: {}", userId);
                return userId;
            }

            // principal이 String 타입인 경우 Long으로 변환 시도
            if (principal instanceof String) {
                String principalStr = (String) principal;

                // 빈 문자열이나 null 체크
                if (principalStr == null || principalStr.trim().isEmpty()) {
                    log.warn("Principal string is null or empty");
                    return null;
                }

                // 숫자가 아닌 경우 (username 등) null 반환
                if (!principalStr.trim().matches("\\d+")) {
                    log.warn("Principal string '{}' is not a valid number (possibly username)", principalStr);
                    return null;
                }

                try {
                    Long userId = Long.parseLong(principalStr.trim());
                    log.debug("Successfully parsed user ID from string principal: {}", userId);
                    return userId;
                } catch (NumberFormatException e) {
                    log.error("Failed to parse user ID from string principal: '{}'", principalStr, e);
                    return null;
                }
            }

            // principal이 Number 타입인 경우
            if (principal instanceof Number) {
                Long userId = ((Number) principal).longValue();
                log.debug("Successfully resolved user ID from Number principal: {}", userId);
                return userId;
            }

            // UserDetails나 UserPrincipal 타입인 경우 리플렉션으로 getId 시도
            try {
                java.lang.reflect.Method getIdMethod = principal.getClass().getMethod("getId");
                Object idValue = getIdMethod.invoke(principal);

                if (idValue instanceof Long) {
                    Long userId = (Long) idValue;
                    log.debug("Successfully resolved user ID from custom principal getId(): {}", userId);
                    return userId;
                } else if (idValue instanceof Number) {
                    Long userId = ((Number) idValue).longValue();
                    log.debug("Successfully resolved user ID from custom principal getId() as Number: {}", userId);
                    return userId;
                } else if (idValue instanceof String) {
                    try {
                        Long userId = Long.parseLong(((String) idValue).trim());
                        log.debug("Successfully parsed user ID from custom principal getId() string: {}", userId);
                        return userId;
                    } catch (NumberFormatException e) {
                        log.error("Failed to parse user ID from custom principal getId() string: '{}'", idValue, e);
                        return null;
                    }
                }
            } catch (NoSuchMethodException e) {
                log.debug("Principal class {} does not have getId() method", principal.getClass().getSimpleName());
            } catch (Exception e) {
                log.error("Error calling getId() on principal", e);
            }

            log.error("Unsupported principal type: {} with value: {}",
                    principal.getClass().getName(), principal);
            return null;

        } catch (Exception e) {
            log.error("Unexpected error in CurrentUserArgumentResolver", e);
            return null;
        }
    }

}
