package com.berryweb.shop.repository;

import com.berryweb.shop.entity.Product;
import com.berryweb.shop.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findByProductAndIsActiveTrueOrderByCreatedAtDesc(Product product, Pageable pageable);
    Page<Review> findByUserIdAndIsActiveTrueOrderByCreatedAtDesc(Long userId, Pageable pageable);
    Optional<Review> findByIdAndIsActiveTrue(Long id);
    List<Review> findByProductAndRatingAndIsActiveTrueOrderByCreatedAtDesc(Product product, Integer rating);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product = :product AND r.isActive = true")
    Optional<BigDecimal> findAverageRatingByProduct(@Param("product") Product product);

    long countByProductAndIsActiveTrue(Product product);
    long countByProductAndRatingAndIsActiveTrue(Product product, Integer rating);
    boolean existsByProductAndUserIdAndIsActiveTrue(Product product, Long userId);

}
