package com.berryweb.shop.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products/{productId}/reviews")
@RequiredArgsConstructor
@Slf4j
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ReviewDto.ReviewInfo>>> getReviewsByProduct(
            @PathVariable Long productId,
            Pageable pageable,
            @RequestHeader("Authorization") String token,
            @CurrentUser Long userId) {

        String authToken = token.replace("Bearer ", "");
        Page<ReviewDto.ReviewInfo> reviews = reviewService.getReviewsByProduct(productId, pageable, authToken, userId);
        return ResponseEntity.ok(ApiResponse.success(reviews));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ReviewDto.ReviewInfo>> createReview(
            @PathVariable Long productId,
            @Valid @RequestBody ReviewDto.CreateReviewRequest request,
            @RequestParam(value = "images", required = false) MultipartFile[] images,
            @RequestHeader("Authorization") String token,
            @CurrentUser Long userId) {

        String authToken = token.replace("Bearer ", "");
        request.setProductId(productId);
        ReviewDto.ReviewInfo review = reviewService.createReview(request, images, authToken, userId);
        return ResponseEntity.ok(ApiResponse.success(review, "리뷰가 작성되었습니다."));
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewDto.ReviewInfo>> updateReview(
            @PathVariable Long productId,
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewDto.UpdateReviewRequest request,
            @RequestHeader("Authorization") String token,
            @CurrentUser Long userId) {

        String authToken = token.replace("Bearer ", "");
        ReviewDto.ReviewInfo review = reviewService.updateReview(reviewId, request, authToken, userId);
        return ResponseEntity.ok(ApiResponse.success(review, "리뷰가 수정되었습니다."));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(
            @PathVariable Long productId,
            @PathVariable Long reviewId,
            @RequestHeader("Authorization") String token,
            @CurrentUser Long userId) {

        String authToken = token.replace("Bearer ", "");
        reviewService.deleteReview(reviewId, authToken, userId);
        return ResponseEntity.ok(ApiResponse.success(null, "리뷰가 삭제되었습니다."));
    }

    @PostMapping("/{reviewId}/helpful")
    public ResponseEntity<ApiResponse<Void>> toggleReviewHelpful(
            @PathVariable Long productId,
            @PathVariable Long reviewId,
            @RequestHeader("Authorization") String token,
            @CurrentUser Long userId) {

        String authToken = token.replace("Bearer ", "");
        reviewService.toggleReviewHelpful(reviewId, authToken, userId);
        return ResponseEntity.ok(ApiResponse.success(null, "도움됨이 처리되었습니다."));
    }

}
