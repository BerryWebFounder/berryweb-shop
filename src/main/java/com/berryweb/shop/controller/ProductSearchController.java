package com.berryweb.shop.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Slf4j
public class ProductSearchController {

    private final ProductService productService;

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductDto.ProductInfo>> getProductById(
            @PathVariable Long productId,
            @RequestHeader("Authorization") String token) {

        String authToken = token.replace("Bearer ", "");
        ProductDto.ProductInfo product = productService.getProductById(productId, authToken);
        return ResponseEntity.ok(ApiResponse.success(product));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<ProductDto.ProductSummary>>> searchProducts(
            @RequestParam String keyword,
            Pageable pageable,
            @RequestHeader("Authorization") String token) {

        String authToken = token.replace("Bearer ", "");
        Page<ProductDto.ProductSummary> products = productService.searchProducts(keyword, pageable, authToken);
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    @GetMapping("/featured")
    public ResponseEntity<ApiResponse<Page<ProductDto.ProductSummary>>> getFeaturedProducts(
            Pageable pageable,
            @RequestHeader("Authorization") String token) {

        String authToken = token.replace("Bearer ", "");
        Page<ProductDto.ProductSummary> products = productService.getFeaturedProducts(pageable, authToken);
        return ResponseEntity.ok(ApiResponse.success(products));
    }

}
