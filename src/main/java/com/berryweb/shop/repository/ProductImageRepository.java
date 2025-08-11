package com.berryweb.shop.repository;

import com.berryweb.shop.entity.Product;
import com.berryweb.shop.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    List<ProductImage> findByProductOrderBySortOrderAsc(Product product);
    Optional<ProductImage> findByProductAndIsMainTrue(Product product);
    List<ProductImage> findByProductAndIsMainFalseOrderBySortOrderAsc(Product product);

    @Modifying
    @Query("UPDATE ProductImage pi SET pi.isMain = false WHERE pi.product = :product")
    void clearMainImages(@Param("product") Product product);

}
