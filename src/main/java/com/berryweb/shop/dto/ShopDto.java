package com.berryweb.shop.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ShopDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShopInfo {
        private Long id;
        private Long ownerUserId;
        private String ownerUsername;
        private String name;
        private String description;
        private String businessNumber;
        private String phone;
        private String email;
        private String address;
        private Boolean isActive;
        private BigDecimal minOrderAmount;
        private BigDecimal deliveryFee;
        private BigDecimal freeDeliveryAmount;
        private String businessHours;
        private Long productCount;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createdAt;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime updatedAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateShopRequest {
        @NotBlank(message = "상점명은 필수입니다.")
        @Size(max = 100, message = "상점명은 100자를 초과할 수 없습니다.")
        private String name;

        @Size(max = 1000, message = "설명은 1000자를 초과할 수 없습니다.")
        private String description;

        @Pattern(regexp = "^\\d{10}-\\d{2}-\\d{5}$", message = "올바른 사업자등록번호 형식이 아닙니다.")
        private String businessNumber;

        @Pattern(regexp = "^[0-9]{10,11}$", message = "올바른 전화번호 형식이 아닙니다.")
        private String phone;

        @Email(message = "올바른 이메일 형식이 아닙니다.")
        private String email;

        private String address;

        @DecimalMin(value = "0", message = "최소 주문금액은 0 이상이어야 합니다.")
        private BigDecimal minOrderAmount = BigDecimal.ZERO;

        @DecimalMin(value = "0", message = "배송비는 0 이상이어야 합니다.")
        private BigDecimal deliveryFee = BigDecimal.ZERO;

        @DecimalMin(value = "0", message = "무료배송 금액은 0 이상이어야 합니다.")
        private BigDecimal freeDeliveryAmount;

        private String businessHours;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateShopRequest {
        @NotBlank(message = "상점명은 필수입니다.")
        @Size(max = 100, message = "상점명은 100자를 초과할 수 없습니다.")
        private String name;

        @Size(max = 1000, message = "설명은 1000자를 초과할 수 없습니다.")
        private String description;

        @Pattern(regexp = "^\\d{10}-\\d{2}-\\d{5}$", message = "올바른 사업자등록번호 형식이 아닙니다.")
        private String businessNumber;

        @Pattern(regexp = "^[0-9]{10,11}$", message = "올바른 전화번호 형식이 아닙니다.")
        private String phone;

        @Email(message = "올바른 이메일 형식이 아닙니다.")
        private String email;

        private String address;
        private BigDecimal minOrderAmount;
        private BigDecimal deliveryFee;
        private BigDecimal freeDeliveryAmount;
        private String businessHours;
        private Boolean isActive;
    }

}
