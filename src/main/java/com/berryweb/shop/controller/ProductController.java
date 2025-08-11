package com.berryweb.shop.controller;

import com.berryweb.shop.common.ApiResponse;
import com.berryweb.shop.dto.ProductDto;
import com.berryweb.shop.security.CurrentUser;
import com.berryweb.shop.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/shops/{shopId}/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProductDto.ProductSummary>>> getProductsByShop(
            @PathVariable Long shopId,
            @RequestParam(required = false) String search,
            Pageable pageable,
            @RequestHeader("Authorization") String token) {

        String authToken = token.replace("Bearer ", "");
        Page<ProductDto.ProductSummary> products = productService.getProductsByShop(shopId, pageable, authToken);
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductDto.ProductInfo>> createProduct(
            @PathVariable Long shopId,
            @Valid @RequestBody ProductDto.CreateProductRequest request,
            @RequestParam(value = "images", required = false) MultipartFile[] images,
            @RequestHeader("Authorization") String token,
            @CurrentUser Long userId) {

        String authToken = token.replace("Bearer ", "");
        request.setShopId(shopId);
        ProductDto.ProductInfo product = productService.createProduct(request, images, authToken, userId);
        return ResponseEntity.ok(ApiResponse.success(product, "상품이 등록되었습니다."));
    }

}
