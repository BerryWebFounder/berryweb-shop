package com.berryweb.shop.service;

import com.berryweb.shop.dto.ProductDto;
import com.berryweb.shop.dto.UserServiceDto;
import com.berryweb.shop.entity.Product;
import com.berryweb.shop.entity.ProductCategory;
import com.berryweb.shop.entity.ProductImage;
import com.berryweb.shop.entity.Shop;
import com.berryweb.shop.exception.CustomException;
import com.berryweb.shop.exception.ErrorCode;
import com.berryweb.shop.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final ShopRepository shopRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductOptionGroupRepository productOptionGroupRepository;
    private final ProductOptionRepository productOptionRepository;
    private final ReviewRepository reviewRepository;
    private final UserServiceClient userServiceClient;
    private final FileService fileService;

    public Page<ProductDto.ProductSummary> getProductsByShop(Long shopId, Pageable pageable, String token) {
        Shop shop = shopRepository.findByIdAndIsActiveTrue(shopId)
                .orElseThrow(() -> new CustomException(ErrorCode.SHOP_NOT_FOUND));

        Page<Product> products = productRepository.findByShopIdAndStatusOrderByCreatedAtDesc(
                shopId, Product.ProductStatus.ACTIVE, pageable);

        return products.map(product -> {
            Optional<ProductImage> mainImage = productImageRepository.findByProductAndIsMainTrue(product);

            ProductDto.ProductImageInfo mainImageInfo = null;
            if (mainImage.isPresent()) {
                ProductImage img = mainImage.get();
                mainImageInfo = ProductDto.ProductImageInfo.builder()
                        .id(img.getId())
                        .originalFilename(img.getOriginalFilename())
                        .storedFilename(img.getStoredFilename())
                        .fileSize(img.getFileSize())
                        .isMain(img.getIsMain())
                        .altText(img.getAltText())
                        .sortOrder(img.getSortOrder())
                        .createdAt(img.getCreatedAt())
                        .build();
            }

            return ProductDto.ProductSummary.builder()
                    .id(product.getId())
                    .name(product.getName())
                    .shortDescription(product.getShortDescription())
                    .price(product.getPrice())
                    .salePrice(product.getSalePrice())
                    .isFeatured(product.getIsFeatured())
                    .ratingAverage(product.getRatingAverage())
                    .ratingCount(product.getRatingCount())
                    .mainImage(mainImageInfo)
                    .createdAt(product.getCreatedAt())
                    .build();
        });
    }

    public ProductDto.ProductInfo getProductById(Long productId, String token) {
        Product product = productRepository.findByIdAndStatus(productId, Product.ProductStatus.ACTIVE)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        // 상품 이미지 조회
        List<ProductDto.ProductImageInfo> images = productImageRepository.findByProductOrderBySortOrderAsc(product)
                .stream()
                .map(img -> ProductDto.ProductImageInfo.builder()
                        .id(img.getId())
                        .originalFilename(img.getOriginalFilename())
                        .storedFilename(img.getStoredFilename())
                        .fileSize(img.getFileSize())
                        .isMain(img.getIsMain())
                        .altText(img.getAltText())
                        .sortOrder(img.getSortOrder())
                        .createdAt(img.getCreatedAt())
                        .build())
                .toList();

        // 상품 옵션 그룹 조회
        List<ProductDto.ProductOptionGroupInfo> optionGroups = productOptionGroupRepository.findByProductOrderBySortOrderAsc(product)
                .stream()
                .map(group -> {
                    List<ProductDto.ProductOptionInfo> options = productOptionRepository.findByOptionGroupAndIsActiveTrueOrderBySortOrderAsc(group)
                            .stream()
                            .map(option -> ProductDto.ProductOptionInfo.builder()
                                    .id(option.getId())
                                    .name(option.getName())
                                    .additionalPrice(option.getAdditionalPrice())
                                    .stockQuantity(option.getStockQuantity())
                                    .isActive(option.getIsActive())
                                    .sortOrder(option.getSortOrder())
                                    .build())
                            .toList();

                    return ProductDto.ProductOptionGroupInfo.builder()
                            .id(group.getId())
                            .name(group.getName())
                            .type(group.getType())
                            .isRequired(group.getIsRequired())
                            .sortOrder(group.getSortOrder())
                            .options(options)
                            .build();
                })
                .toList();

        return ProductDto.ProductInfo.builder()
                .id(product.getId())
                .shopId(product.getShop().getId())
                .shopName(product.getShop().getName())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .name(product.getName())
                .description(product.getDescription())
                .shortDescription(product.getShortDescription())
                .price(product.getPrice())
                .salePrice(product.getSalePrice())
                .stockQuantity(product.getStockQuantity())
                .minStockQuantity(product.getMinStockQuantity())
                .maxOrderQuantity(product.getMaxOrderQuantity())
                .trackStock(product.getTrackStock())
                .status(product.getStatus())
                .isFeatured(product.getIsFeatured())
                .slug(product.getSlug())
                .metaTitle(product.getMetaTitle())
                .metaDescription(product.getMetaDescription())
                .weight(product.getWeight())
                .dimensions(product.getDimensions())
                .ratingAverage(product.getRatingAverage())
                .ratingCount(product.getRatingCount())
                .images(images)
                .optionGroups(optionGroups)
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    @Transactional
    public ProductDto.ProductInfo createProduct(ProductDto.CreateProductRequest request, MultipartFile[] images, String token, Long userId) {
        Shop shop = shopRepository.findByIdAndIsActiveTrue(request.getShopId())
                .orElseThrow(() -> new CustomException(ErrorCode.SHOP_NOT_FOUND));

        UserServiceDto.UserInfo userInfo = userServiceClient.getUserInfo(userId, token);

        // 상점 소유자이거나 ADMIN만 상품 생성 가능
        if (!shop.getOwnerUserId().equals(userId) &&
                (userInfo == null || userInfo.getRole() != UserServiceDto.UserInfo.UserRole.ADMIN)) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }

        ProductCategory category = null;
        if (request.getCategoryId() != null) {
            category = productCategoryRepository.findByIdAndShopAndIsActiveTrue(request.getCategoryId(), shop)
                    .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));
        }

        // slug 중복 확인
        if (request.getSlug() != null && productRepository.findBySlug(request.getSlug()).isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATE_SLUG);
        }

        Product product = Product.builder()
                .shop(shop)
                .category(category)
                .name(request.getName())
                .description(request.getDescription())
                .shortDescription(request.getShortDescription())
                .price(request.getPrice())
                .salePrice(request.getSalePrice())
                .costPrice(request.getCostPrice())
                .stockQuantity(request.getStockQuantity())
                .minStockQuantity(request.getMinStockQuantity())
                .maxOrderQuantity(request.getMaxOrderQuantity())
                .trackStock(request.getTrackStock())
                .isFeatured(request.getIsFeatured())
                .slug(request.getSlug())
                .metaTitle(request.getMetaTitle())
                .metaDescription(request.getMetaDescription())
                .weight(request.getWeight())
                .dimensions(request.getDimensions())
                .createdBy(userId)
                .build();

        product = productRepository.save(product);

        // 이미지 업로드 처리
        List<ProductDto.ProductImageInfo> imageInfos = new ArrayList<>();
        if (images != null && images.length > 0) {
            imageInfos = fileService.saveProductImages(product, images, userId);
        }

        return ProductDto.ProductInfo.builder()
                .id(product.getId())
                .shopId(product.getShop().getId())
                .shopName(product.getShop().getName())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .name(product.getName())
                .description(product.getDescription())
                .shortDescription(product.getShortDescription())
                .price(product.getPrice())
                .salePrice(product.getSalePrice())
                .stockQuantity(product.getStockQuantity())
                .minStockQuantity(product.getMinStockQuantity())
                .maxOrderQuantity(product.getMaxOrderQuantity())
                .trackStock(product.getTrackStock())
                .status(product.getStatus())
                .isFeatured(product.getIsFeatured())
                .slug(product.getSlug())
                .metaTitle(product.getMetaTitle())
                .metaDescription(product.getMetaDescription())
                .weight(product.getWeight())
                .dimensions(product.getDimensions())
                .ratingAverage(product.getRatingAverage())
                .ratingCount(product.getRatingCount())
                .images(imageInfos)
                .optionGroups(new ArrayList<>())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    public Page<ProductDto.ProductSummary> searchProducts(String keyword, Pageable pageable, String token) {
        Page<Product> products = productRepository.searchAllByKeyword(Product.ProductStatus.ACTIVE, keyword, pageable);

        return products.map(product -> {
            Optional<ProductImage> mainImage = productImageRepository.findByProductAndIsMainTrue(product);

            ProductDto.ProductImageInfo mainImageInfo = null;
            if (mainImage.isPresent()) {
                ProductImage img = mainImage.get();
                mainImageInfo = ProductDto.ProductImageInfo.builder()
                        .id(img.getId())
                        .originalFilename(img.getOriginalFilename())
                        .storedFilename(img.getStoredFilename())
                        .fileSize(img.getFileSize())
                        .isMain(img.getIsMain())
                        .altText(img.getAltText())
                        .sortOrder(img.getSortOrder())
                        .createdAt(img.getCreatedAt())
                        .build();
            }

            return ProductDto.ProductSummary.builder()
                    .id(product.getId())
                    .name(product.getName())
                    .shortDescription(product.getShortDescription())
                    .price(product.getPrice())
                    .salePrice(product.getSalePrice())
                    .isFeatured(product.getIsFeatured())
                    .ratingAverage(product.getRatingAverage())
                    .ratingCount(product.getRatingCount())
                    .mainImage(mainImageInfo)
                    .createdAt(product.getCreatedAt())
                    .build();
        });
    }

    public Page<ProductDto.ProductSummary> getFeaturedProducts(Pageable pageable, String token) {
        Page<Product> products = productRepository.findByIsFeaturedTrueAndStatusOrderByCreatedAtDesc(
                Product.ProductStatus.ACTIVE, pageable);

        return products.map(product -> {
            Optional<ProductImage> mainImage = productImageRepository.findByProductAndIsMainTrue(product);

            ProductDto.ProductImageInfo mainImageInfo = null;
            if (mainImage.isPresent()) {
                ProductImage img = mainImage.get();
                mainImageInfo = ProductDto.ProductImageInfo.builder()
                        .id(img.getId())
                        .originalFilename(img.getOriginalFilename())
                        .storedFilename(img.getStoredFilename())
                        .fileSize(img.getFileSize())
                        .isMain(img.getIsMain())
                        .altText(img.getAltText())
                        .sortOrder(img.getSortOrder())
                        .createdAt(img.getCreatedAt())
                        .build();
            }

            return ProductDto.ProductSummary.builder()
                    .id(product.getId())
                    .name(product.getName())
                    .shortDescription(product.getShortDescription())
                    .price(product.getPrice())
                    .salePrice(product.getSalePrice())
                    .isFeatured(product.getIsFeatured())
                    .ratingAverage(product.getRatingAverage())
                    .ratingCount(product.getRatingCount())
                    .mainImage(mainImageInfo)
                    .createdAt(product.getCreatedAt())
                    .build();
        });
    }

}
