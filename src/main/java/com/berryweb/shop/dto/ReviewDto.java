package com.berryweb.shop.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class ReviewDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReviewInfo {
        private Long id;
        private Long productId;
        private String productName;
        private Long userId;
        private String username;
        private Integer rating;
        private String title;
        private String content;
        private Boolean isVerifiedPurchase;
        private Boolean isActive;
        private Integer helpfulCount;
        private Boolean isHelpful;
        private List<ReviewImageInfo> images;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createdAt;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime updatedAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateReviewRequest {
        @NotNull(message = "상품 ID는 필수입니다.")
        private Long productId;

        @NotNull(message = "평점은 필수입니다.")
        @Min(value = 1, message = "평점은 1 이상이어야 합니다.")
        @Max(value = 5, message = "평점은 5 이하여야 합니다.")
        private Integer rating;

        @Size(max = 200, message = "제목은 200자를 초과할 수 없습니다.")
        private String title;

        @NotBlank(message = "리뷰 내용은 필수입니다.")
        @Size(max = 2000, message = "리뷰 내용은 2000자를 초과할 수 없습니다.")
        private String content;

        private Boolean isVerifiedPurchase = false;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateReviewRequest {
        @NotNull(message = "평점은 필수입니다.")
        @Min(value = 1, message = "평점은 1 이상이어야 합니다.")
        @Max(value = 5, message = "평점은 5 이하여야 합니다.")
        private Integer rating;

        @Size(max = 200, message = "제목은 200자를 초과할 수 없습니다.")
        private String title;

        @NotBlank(message = "리뷰 내용은 필수입니다.")
        @Size(max = 2000, message = "리뷰 내용은 2000자를 초과할 수 없습니다.")
        private String content;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReviewImageInfo {
        private Long id;
        private String originalFilename;
        private String storedFilename;
        private Long fileSize;
        private Integer sortOrder;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createdAt;
    }

}
