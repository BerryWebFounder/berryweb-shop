package com.berryweb.shop.repository;

import com.berryweb.shop.entity.Review;
import com.berryweb.shop.entity.ReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> {

    List<ReviewImage> findByReviewOrderBySortOrderAsc(Review review);

}
