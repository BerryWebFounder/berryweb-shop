package com.berryweb.shop.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class ProductCategoryDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryInfo {
        private Long id;
        private Long shopId;
        private Long parentId;
        private String name;
        private String description;
        private Integer sortOrder;
        private Boolean isActive;
        private List<CategoryInfo> children;
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
    public static class CreateCategoryRequest {
        @NotNull(message = "상점 ID는 필수입니다.")
        private Long shopId;

        private Long parentId;

        @NotBlank(message = "카테고리명은 필수입니다.")
        @Size(max = 100, message = "카테고리명은 100자를 초과할 수 없습니다.")
        private String name;

        @Size(max = 500, message = "설명은 500자를 초과할 수 없습니다.")
        private String description;

        private Integer sortOrder = 0;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateCategoryRequest {
        private Long parentId;

        @NotBlank(message = "카테고리명은 필수입니다.")
        @Size(max = 100, message = "카테고리명은 100자를 초과할 수 없습니다.")
        private String name;

        @Size(max = 500, message = "설명은 500자를 초과할 수 없습니다.")
        private String description;

        private Integer sortOrder;
        private Boolean isActive;
    }

}
