package com.berryweb.shop.controller;

import com.berryweb.shop.common.ApiResponse;
import com.berryweb.shop.dto.ProductDto;
import com.berryweb.shop.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/products")
@RequiredArgsConstructor
@Slf4j
public class ProductSearchController {

    private final ProductService productService;

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductDto.ProductInfo>> getProductById(
            @PathVariable Long productId,
            @RequestHeader(value = "Authorization", required = false) String token) {

        String authToken = token != null ? token.replace("Bearer ", "") : "";
        ProductDto.ProductInfo product = productService.getProductById(productId, authToken);
        return ResponseEntity.ok(ApiResponse.success(product));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<ProductDto.ProductSummary>>> searchProducts(
            @RequestParam String keyword,
            Pageable pageable,
            @RequestHeader(value = "Authorization", required = false) String token) {

        String authToken = token != null ? token.replace("Bearer ", "") : "";
        Page<ProductDto.ProductSummary> products = productService.searchProducts(keyword, pageable, authToken);
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    @GetMapping("/featured")
    public ResponseEntity<ApiResponse<Page<ProductDto.ProductSummary>>> getFeaturedProducts(
            Pageable pageable,
            @RequestHeader(value = "Authorization", required = false) String token) {

        String authToken = token != null ? token.replace("Bearer ", "") : "";
        Page<ProductDto.ProductSummary> products = productService.getFeaturedProducts(pageable, authToken);
        return ResponseEntity.ok(ApiResponse.success(products));
    }

}
