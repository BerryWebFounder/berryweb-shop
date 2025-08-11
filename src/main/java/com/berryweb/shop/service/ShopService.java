package com.berryweb.shop.service;

import com.berryweb.shop.dto.ShopDto;
import com.berryweb.shop.dto.UserServiceDto;
import com.berryweb.shop.entity.Product;
import com.berryweb.shop.entity.Shop;
import com.berryweb.shop.exception.CustomException;
import com.berryweb.shop.exception.ErrorCode;
import com.berryweb.shop.repository.ProductRepository;
import com.berryweb.shop.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ShopService {

    private final ShopRepository shopRepository;
    private final ProductRepository productRepository;
    private final UserServiceClient userServiceClient;

    public Page<ShopDto.ShopInfo> getAllShops(Pageable pageable, String token) {
        return shopRepository.findByIsActiveTrueOrderByCreatedAtDesc(pageable)
                .map(shop -> {
                    UserServiceDto.UserInfo ownerInfo = userServiceClient.getUserInfo(shop.getOwnerUserId(), token);
                    long productCount = productRepository.countByShopAndStatus(shop, Product.ProductStatus.ACTIVE);

                    return ShopDto.ShopInfo.builder()
                            .id(shop.getId())
                            .ownerUserId(shop.getOwnerUserId())
                            .ownerUsername(ownerInfo != null ? ownerInfo.getUsername() : "알 수 없음")
                            .name(shop.getName())
                            .description(shop.getDescription())
                            .businessNumber(shop.getBusinessNumber())
                            .phone(shop.getPhone())
                            .email(shop.getEmail())
                            .address(shop.getAddress())
                            .isActive(shop.getIsActive())
                            .minOrderAmount(shop.getMinOrderAmount())
                            .deliveryFee(shop.getDeliveryFee())
                            .freeDeliveryAmount(shop.getFreeDeliveryAmount())
                            .businessHours(shop.getBusinessHours())
                            .productCount(productCount)
                            .createdAt(shop.getCreatedAt())
                            .updatedAt(shop.getUpdatedAt())
                            .build();
                });
    }

    public ShopDto.ShopInfo getShopById(Long shopId, String token) {
        Shop shop = shopRepository.findByIdAndIsActiveTrue(shopId)
                .orElseThrow(() -> new CustomException(ErrorCode.SHOP_NOT_FOUND));

        UserServiceDto.UserInfo ownerInfo = userServiceClient.getUserInfo(shop.getOwnerUserId(), token);
        long productCount = productRepository.countByShopAndStatus(shop, Product.ProductStatus.ACTIVE);

        return ShopDto.ShopInfo.builder()
                .id(shop.getId())
                .ownerUserId(shop.getOwnerUserId())
                .ownerUsername(ownerInfo != null ? ownerInfo.getUsername() : "알 수 없음")
                .name(shop.getName())
                .description(shop.getDescription())
                .businessNumber(shop.getBusinessNumber())
                .phone(shop.getPhone())
                .email(shop.getEmail())
                .address(shop.getAddress())
                .isActive(shop.getIsActive())
                .minOrderAmount(shop.getMinOrderAmount())
                .deliveryFee(shop.getDeliveryFee())
                .freeDeliveryAmount(shop.getFreeDeliveryAmount())
                .businessHours(shop.getBusinessHours())
                .productCount(productCount)
                .createdAt(shop.getCreatedAt())
                .updatedAt(shop.getUpdatedAt())
                .build();
    }

    @Transactional
    public ShopDto.ShopInfo createShop(ShopDto.CreateShopRequest request, String token, Long userId) {
        UserServiceDto.UserInfo userInfo = userServiceClient.getUserInfo(userId, token);

        if (userInfo == null) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        Shop shop = Shop.builder()
                .ownerUserId(userId)
                .name(request.getName())
                .description(request.getDescription())
                .businessNumber(request.getBusinessNumber())
                .phone(request.getPhone())
                .email(request.getEmail())
                .address(request.getAddress())
                .minOrderAmount(request.getMinOrderAmount())
                .deliveryFee(request.getDeliveryFee())
                .freeDeliveryAmount(request.getFreeDeliveryAmount())
                .businessHours(request.getBusinessHours())
                .createdBy(userId)
                .build();

        shop = shopRepository.save(shop);

        return ShopDto.ShopInfo.builder()
                .id(shop.getId())
                .ownerUserId(shop.getOwnerUserId())
                .ownerUsername(userInfo.getUsername())
                .name(shop.getName())
                .description(shop.getDescription())
                .businessNumber(shop.getBusinessNumber())
                .phone(shop.getPhone())
                .email(shop.getEmail())
                .address(shop.getAddress())
                .isActive(shop.getIsActive())
                .minOrderAmount(shop.getMinOrderAmount())
                .deliveryFee(shop.getDeliveryFee())
                .freeDeliveryAmount(shop.getFreeDeliveryAmount())
                .businessHours(shop.getBusinessHours())
                .productCount(0L)
                .createdAt(shop.getCreatedAt())
                .updatedAt(shop.getUpdatedAt())
                .build();
    }

    @Transactional
    public ShopDto.ShopInfo updateShop(Long shopId, ShopDto.UpdateShopRequest request, String token, Long userId) {
        Shop shop = shopRepository.findByIdAndIsActiveTrue(shopId)
                .orElseThrow(() -> new CustomException(ErrorCode.SHOP_NOT_FOUND));

        UserServiceDto.UserInfo userInfo = userServiceClient.getUserInfo(userId, token);

        // 상점 소유자이거나 ADMIN만 수정 가능
        if (!shop.getOwnerUserId().equals(userId) &&
                (userInfo == null || userInfo.getRole() != UserServiceDto.UserInfo.UserRole.ADMIN)) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }

        shop.setName(request.getName());
        shop.setDescription(request.getDescription());
        shop.setBusinessNumber(request.getBusinessNumber());
        shop.setPhone(request.getPhone());
        shop.setEmail(request.getEmail());
        shop.setAddress(request.getAddress());

        if (request.getMinOrderAmount() != null) {
            shop.setMinOrderAmount(request.getMinOrderAmount());
        }
        if (request.getDeliveryFee() != null) {
            shop.setDeliveryFee(request.getDeliveryFee());
        }
        if (request.getFreeDeliveryAmount() != null) {
            shop.setFreeDeliveryAmount(request.getFreeDeliveryAmount());
        }
        if (request.getBusinessHours() != null) {
            shop.setBusinessHours(request.getBusinessHours());
        }
        if (request.getIsActive() != null) {
            shop.setIsActive(request.getIsActive());
        }

        shop.setUpdatedBy(userId);
        shop = shopRepository.save(shop);

        UserServiceDto.UserInfo ownerInfo = userServiceClient.getUserInfo(shop.getOwnerUserId(), token);
        long productCount = productRepository.countByShopAndStatus(shop, Product.ProductStatus.ACTIVE);

        return ShopDto.ShopInfo.builder()
                .id(shop.getId())
                .ownerUserId(shop.getOwnerUserId())
                .ownerUsername(ownerInfo != null ? ownerInfo.getUsername() : "알 수 없음")
                .name(shop.getName())
                .description(shop.getDescription())
                .businessNumber(shop.getBusinessNumber())
                .phone(shop.getPhone())
                .email(shop.getEmail())
                .address(shop.getAddress())
                .isActive(shop.getIsActive())
                .minOrderAmount(shop.getMinOrderAmount())
                .deliveryFee(shop.getDeliveryFee())
                .freeDeliveryAmount(shop.getFreeDeliveryAmount())
                .businessHours(shop.getBusinessHours())
                .productCount(productCount)
                .createdAt(shop.getCreatedAt())
                .updatedAt(shop.getUpdatedAt())
                .build();
    }

    public List<ShopDto.ShopInfo> getMyShops(String token, Long userId) {
        List<Shop> shops = shopRepository.findByOwnerUserIdAndIsActiveTrue(userId);
        UserServiceDto.UserInfo userInfo = userServiceClient.getUserInfo(userId, token);

        return shops.stream()
                .map(shop -> {
                    long productCount = productRepository.countByShopAndStatus(shop, Product.ProductStatus.ACTIVE);

                    return ShopDto.ShopInfo.builder()
                            .id(shop.getId())
                            .ownerUserId(shop.getOwnerUserId())
                            .ownerUsername(userInfo != null ? userInfo.getUsername() : "알 수 없음")
                            .name(shop.getName())
                            .description(shop.getDescription())
                            .businessNumber(shop.getBusinessNumber())
                            .phone(shop.getPhone())
                            .email(shop.getEmail())
                            .address(shop.getAddress())
                            .isActive(shop.getIsActive())
                            .minOrderAmount(shop.getMinOrderAmount())
                            .deliveryFee(shop.getDeliveryFee())
                            .freeDeliveryAmount(shop.getFreeDeliveryAmount())
                            .businessHours(shop.getBusinessHours())
                            .productCount(productCount)
                            .createdAt(shop.getCreatedAt())
                            .updatedAt(shop.getUpdatedAt())
                            .build();
                })
                .toList();
    }

    public Page<ShopDto.ShopInfo> searchShops(String keyword, Pageable pageable, String token) {
        return shopRepository.findByNameContainingAndIsActiveTrueOrderByCreatedAtDesc(keyword, pageable)
                .map(shop -> {
                    UserServiceDto.UserInfo ownerInfo = userServiceClient.getUserInfo(shop.getOwnerUserId(), token);
                    long productCount = productRepository.countByShopAndStatus(shop, Product.ProductStatus.ACTIVE);

                    return ShopDto.ShopInfo.builder()
                            .id(shop.getId())
                            .ownerUserId(shop.getOwnerUserId())
                            .ownerUsername(ownerInfo != null ? ownerInfo.getUsername() : "알 수 없음")
                            .name(shop.getName())
                            .description(shop.getDescription())
                            .businessNumber(shop.getBusinessNumber())
                            .phone(shop.getPhone())
                            .email(shop.getEmail())
                            .address(shop.getAddress())
                            .isActive(shop.getIsActive())
                            .minOrderAmount(shop.getMinOrderAmount())
                            .deliveryFee(shop.getDeliveryFee())
                            .freeDeliveryAmount(shop.getFreeDeliveryAmount())
                            .businessHours(shop.getBusinessHours())
                            .productCount(productCount)
                            .createdAt(shop.getCreatedAt())
                            .updatedAt(shop.getUpdatedAt())
                            .build();
                });
    }

}
