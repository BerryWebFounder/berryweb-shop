package com.berryweb.shop.repository;

import com.berryweb.shop.entity.Review;
import com.berryweb.shop.entity.ReviewHelpful;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewHelpfulRepository extends JpaRepository<ReviewHelpful, Long> {

    Optional<ReviewHelpful> findByReviewAndUserId(Review review, Long userId);
    boolean existsByReviewAndUserId(Review review, Long userId);
    long countByReview(Review review);

    @Modifying
    @Query("DELETE FROM ReviewHelpful rh WHERE rh.review = :review AND rh.userId = :userId")
    void deleteByReviewAndUserId(@Param("review") Review review, @Param("userId") Long userId);

}
