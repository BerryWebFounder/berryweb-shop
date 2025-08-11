package com.berryweb.shop.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/shops")
@RequiredArgsConstructor
@Slf4j
public class ShopController {

    private final ShopService shopService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ShopDto.ShopInfo>>> getAllShops(
            @RequestParam(required = false) String search,
            Pageable pageable,
            @RequestHeader("Authorization") String token) {

        String authToken = token.replace("Bearer ", "");
        Page<ShopDto.ShopInfo> shops;

        if (search != null && !search.trim().isEmpty()) {
            shops = shopService.searchShops(search, pageable, authToken);
        } else {
            shops = shopService.getAllShops(pageable, authToken);
        }

        return ResponseEntity.ok(ApiResponse.success(shops));
    }

    @GetMapping("/{shopId}")
    public ResponseEntity<ApiResponse<ShopDto.ShopInfo>> getShopById(
            @PathVariable Long shopId,
            @RequestHeader("Authorization") String token) {

        String authToken = token.replace("Bearer ", "");
        ShopDto.ShopInfo shop = shopService.getShopById(shopId, authToken);
        return ResponseEntity.ok(ApiResponse.success(shop));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<ShopDto.ShopInfo>>> getMyShops(
            @RequestHeader("Authorization") String token,
            @CurrentUser Long userId) {

        String authToken = token.replace("Bearer ", "");
        List<ShopDto.ShopInfo> shops = shopService.getMyShops(authToken, userId);
        return ResponseEntity.ok(ApiResponse.success(shops));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ShopDto.ShopInfo>> createShop(
            @Valid @RequestBody ShopDto.CreateShopRequest request,
            @RequestHeader("Authorization") String token,
            @CurrentUser Long userId) {

        String authToken = token.replace("Bearer ", "");
        ShopDto.ShopInfo shop = shopService.createShop(request, authToken, userId);
        return ResponseEntity.ok(ApiResponse.success(shop, "상점이 생성되었습니다."));
    }

    @PutMapping("/{shopId}")
    public ResponseEntity<ApiResponse<ShopDto.ShopInfo>> updateShop(
            @PathVariable Long shopId,
            @Valid @RequestBody ShopDto.UpdateShopRequest request,
            @RequestHeader("Authorization") String token,
            @CurrentUser Long userId) {

        String authToken = token.replace("Bearer ", "");
        ShopDto.ShopInfo shop = shopService.updateShop(shopId, request, authToken, userId);
        return ResponseEntity.ok(ApiResponse.success(shop, "상점 정보가 수정되었습니다."));
    }

}
