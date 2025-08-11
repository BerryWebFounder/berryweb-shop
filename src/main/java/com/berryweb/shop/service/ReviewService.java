package com.berryweb.shop.service;

import com.berryweb.shop.dto.ReviewDto;
import com.berryweb.shop.dto.UserServiceDto;
import com.berryweb.shop.entity.Product;
import com.berryweb.shop.entity.Review;
import com.berryweb.shop.entity.ReviewHelpful;
import com.berryweb.shop.exception.CustomException;
import com.berryweb.shop.exception.ErrorCode;
import com.berryweb.shop.repository.ProductRepository;
import com.berryweb.shop.repository.ReviewHelpfulRepository;
import com.berryweb.shop.repository.ReviewImageRepository;
import com.berryweb.shop.repository.ReviewRepository;
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
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final ReviewHelpfulRepository reviewHelpfulRepository;
    private final UserServiceClient userServiceClient;
    private final FileService fileService;

    public Page<ReviewDto.ReviewInfo> getReviewsByProduct(Long productId, Pageable pageable, String token, Long userId) {
        Product product = productRepository.findByIdAndStatus(productId, Product.ProductStatus.ACTIVE)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        Page<Review> reviews = reviewRepository.findByProductAndIsActiveTrueOrderByCreatedAtDesc(product, pageable);

        return reviews.map(review -> buildReviewInfo(review, token, userId));
    }

    @Transactional
    public ReviewDto.ReviewInfo createReview(ReviewDto.CreateReviewRequest request, MultipartFile[] images, String token, Long userId) {
        Product product = productRepository.findByIdAndStatus(request.getProductId(), Product.ProductStatus.ACTIVE)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        // 중복 리뷰 확인
        if (reviewRepository.existsByProductAndUserIdAndIsActiveTrue(product, userId)) {
            throw new CustomException(ErrorCode.DUPLICATE_REVIEW);
        }

        Review review = Review.builder()
                .product(product)
                .userId(userId)
                .rating(request.getRating())
                .title(request.getTitle())
                .content(request.getContent())
                .isVerifiedPurchase(request.getIsVerifiedPurchase())
                .createdBy(userId)
                .build();

        review = reviewRepository.save(review);

        // 이미지 업로드 처리
        List<ReviewDto.ReviewImageInfo> imageInfos = new ArrayList<>();
        if (images != null && images.length > 0) {
            imageInfos = fileService.saveReviewImages(review, images);
        }

        return buildReviewInfo(review, token, userId);
    }

    @Transactional
    public ReviewDto.ReviewInfo updateReview(Long reviewId, ReviewDto.UpdateReviewRequest request, String token, Long userId) {
        Review review = reviewRepository.findByIdAndIsActiveTrue(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

        // 작성자만 수정 가능
        if (!review.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }

        review.setRating(request.getRating());
        review.setTitle(request.getTitle());
        review.setContent(request.getContent());
        review.setUpdatedBy(userId);

        review = reviewRepository.save(review);

        return buildReviewInfo(review, token, userId);
    }

    @Transactional
    public void deleteReview(Long reviewId, String token, Long userId) {
        Review review = reviewRepository.findByIdAndIsActiveTrue(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

        UserServiceDto.UserInfo userInfo = userServiceClient.getUserInfo(userId, token);

        // 작성자이거나 ADMIN만 삭제 가능
        if (!review.getUserId().equals(userId) &&
                (userInfo == null || userInfo.getRole() != UserServiceDto.UserInfo.UserRole.ADMIN)) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }

        review.setIsActive(false);
        review.setUpdatedBy(userId);
        reviewRepository.save(review);
    }

    @Transactional
    public void toggleReviewHelpful(Long reviewId, String token, Long userId) {
        Review review = reviewRepository.findByIdAndIsActiveTrue(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

        Optional<ReviewHelpful> existingVote = reviewHelpfulRepository.findByReviewAndUserId(review, userId);

        if (existingVote.isPresent()) {
            // 도움됨 취소
            reviewHelpfulRepository.delete(existingVote.get());
        } else {
            // 도움됨 추가
            ReviewHelpful helpful = ReviewHelpful.builder()
                    .review(review)
                    .userId(userId)
                    .build();
            reviewHelpfulRepository.save(helpful);
        }
    }

    private ReviewDto.ReviewInfo buildReviewInfo(Review review, String token, Long userId) {
        UserServiceDto.UserInfo userInfo = userServiceClient.getUserInfo(review.getUserId(), token);
        boolean isHelpful = reviewHelpfulRepository.existsByReviewAndUserId(review, userId);

        List<ReviewDto.ReviewImageInfo> images = reviewImageRepository.findByReviewOrderBySortOrderAsc(review)
                .stream()
                .map(img -> ReviewDto.ReviewImageInfo.builder()
                        .id(img.getId())
                        .originalFilename(img.getOriginalFilename())
                        .storedFilename(img.getStoredFilename())
                        .fileSize(img.getFileSize())
                        .sortOrder(img.getSortOrder())
                        .createdAt(img.getCreatedAt())
                        .build())
                .toList();

        return ReviewDto.ReviewInfo.builder()
                .id(review.getId())
                .productId(review.getProduct().getId())
                .productName(review.getProduct().getName())
                .userId(review.getUserId())
                .username(userInfo != null ? userInfo.getUsername() : "알 수 없음")
                .rating(review.getRating())
                .title(review.getTitle())
                .content(review.getContent())
                .isVerifiedPurchase(review.getIsVerifiedPurchase())
                .isActive(review.getIsActive())
                .helpfulCount(review.getHelpfulCount())
                .isHelpful(isHelpful)
                .images(images)
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }

}
