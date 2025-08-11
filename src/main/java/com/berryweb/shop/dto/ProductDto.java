package com.berryweb.shop.dto;

import com.berryweb.shop.entity.Product;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ProductDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductInfo {
        private Long id;
        private Long shopId;
        private String shopName;
        private Long categoryId;
        private String categoryName;
        private String name;
        private String description;
        private String shortDescription;
        private BigDecimal price;
        private BigDecimal salePrice;
        private Integer stockQuantity;
        private Integer minStockQuantity;
        private Integer maxOrderQuantity;
        private Boolean trackStock;
        private Product.ProductStatus status;
        private Boolean isFeatured;
        private String slug;
        private String metaTitle;
        private String metaDescription;
        private BigDecimal weight;
        private String dimensions;
        private BigDecimal ratingAverage;
        private Integer ratingCount;
        private List<ProductImageInfo> images;
        private List<ProductOptionGroupInfo> optionGroups;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createdAt;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime updatedAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductSummary {
        private Long id;
        private String name;
        private String shortDescription;
        private BigDecimal price;
        private BigDecimal salePrice;
        private Boolean isFeatured;
        private BigDecimal ratingAverage;
        private Integer ratingCount;
        private ProductImageInfo mainImage;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createdAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateProductRequest {
        @NotNull(message = "상점 ID는 필수입니다.")
        private Long shopId;

        private Long categoryId;

        @NotBlank(message = "상품명은 필수입니다.")
        @Size(max = 200, message = "상품명은 200자를 초과할 수 없습니다.")
        private String name;

        private String description;
        private String shortDescription;

        @NotNull(message = "가격은 필수입니다.")
        @DecimalMin(value = "0", message = "가격은 0 이상이어야 합니다.")
        private BigDecimal price;

        @DecimalMin(value = "0", message = "할인가격은 0 이상이어야 합니다.")
        private BigDecimal salePrice;

        @DecimalMin(value = "0", message = "원가는 0 이상이어야 합니다.")
        private BigDecimal costPrice;

        @Min(value = 0, message = "재고량은 0 이상이어야 합니다.")
        private Integer stockQuantity = 0;

        private Integer minStockQuantity = 0;
        private Integer maxOrderQuantity;
        private Boolean trackStock = true;
        private Boolean isFeatured = false;
        private String slug;
        private String metaTitle;
        private String metaDescription;
        private BigDecimal weight;
        private String dimensions;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateProductRequest {
        private Long categoryId;

        @NotBlank(message = "상품명은 필수입니다.")
        @Size(max = 200, message = "상품명은 200자를 초과할 수 없습니다.")
        private String name;

        private String description;
        private String shortDescription;

        @NotNull(message = "가격은 필수입니다.")
        @DecimalMin(value = "0", message = "가격은 0 이상이어야 합니다.")
        private BigDecimal price;

        private BigDecimal salePrice;
        private BigDecimal costPrice;
        private Integer stockQuantity;
        private Integer minStockQuantity;
        private Integer maxOrderQuantity;
        private Boolean trackStock;
        private Product.ProductStatus status;
        private Boolean isFeatured;
        private String slug;
        private String metaTitle;
        private String metaDescription;
        private BigDecimal weight;
        private String dimensions;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductImageInfo {
        private Long id;
        private String originalFilename;
        private String storedFilename;
        private Long fileSize;
        private Boolean isMain;
        private String altText;
        private Integer sortOrder;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createdAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductOptionGroupInfo {
        private Long id;
        private String name;
        private ProductOptionGroup.OptionType type;
        private Boolean isRequired;
        private Integer sortOrder;
        private List<ProductOptionInfo> options;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductOptionInfo {
        private Long id;
        private String name;
        private BigDecimal additionalPrice;
        private Integer stockQuantity;
        private Boolean isActive;
        private Integer sortOrder;
    }

}
